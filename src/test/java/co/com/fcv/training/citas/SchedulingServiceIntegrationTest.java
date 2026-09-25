package co.com.fcv.training.citas;

import co.com.fcv.training.citas.application.SchedulingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import java.time.Duration;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.*;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class SchedulingServiceIntegrationTest {
    private static final Logger log=LoggerFactory.getLogger(SchedulingServiceIntegrationTest.class);
    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4")
            .withStartupTimeout(Duration.ofMinutes(5)).withStartupTimeoutSeconds(300);
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", MYSQL::getJdbcUrl); r.add("spring.datasource.username", MYSQL::getUsername); r.add("spring.datasource.password", MYSQL::getPassword);
        r.add("app.jwt.access-secret", () -> "a".repeat(40)); r.add("app.jwt.refresh-secret", () -> "b".repeat(40)); r.add("app.cookie.secure", () -> true); r.add("app.cookie.same-site", () -> "None");
    }
    @Autowired SchedulingService scheduling; @Autowired JdbcTemplate jdbc; @Autowired MockMvc mockMvc; @Autowired ObjectMapper objectMapper;
    @Test void retainsConsecutiveSlotsAndReleasesThemAfterAdministrativeRejection() {
        String suffix=UUID.randomUUID().toString();
        Long professionalUser=user("prof-"+suffix+"@example.test",suffix); Long professional=scheduling.createProfessional("Pro","Fes","CC","P"+suffix,"pro2-"+suffix+"@example.test","300", "hash", "PC"+suffix,"LIC"+suffix);
        // createProfessional creates a second user; use that owner for the professional block
        Long owner=jdbc.queryForObject("select user_id from professionals where id=?",Long.class,professional);
        Long specialty=scheduling.createSpecialty("ESP"+suffix,"Especialidad "+suffix,60,false).id(); Long location=jdbc.queryForObject("select id from locations where active=true limit 1",Long.class);
        scheduling.setProfessionalSpecialties(professional,List.of(specialty),specialty); scheduling.setProfessionalLocations(professional,List.of(location));
        LocalDate date=LocalDate.now().plusDays(2); scheduling.createBlock(owner,location,date,LocalTime.of(8,0),LocalTime.of(10,0));
        assertThat(scheduling.availability(location,specialty,professional,date)).anyMatch(a -> a.startAt().equals(LocalDateTime.of(date,LocalTime.of(8,0))) && a.endAt().equals(LocalDateTime.of(date,LocalTime.of(9,0))));
        Long patient=user("patient-"+suffix+"@example.test","U"+suffix); Long admin=user("admin-"+suffix+"@example.test","A"+suffix);
        SchedulingService.Appointment appointment=scheduling.reserve(patient,professional,location,specialty,LocalDateTime.of(date,LocalTime.of(8,0)),"Prueba");
        assertThat(appointment.status()).isEqualTo("REQUESTED");
        assertThatThrownBy(() -> scheduling.reserve(patient,professional,location,specialty,LocalDateTime.of(date,LocalTime.of(8,0)),"Duplicada")).hasMessageContaining("Franja");
        assertThat(scheduling.decide(admin,appointment.id(),"REJECT","Sin disponibilidad clínica").status()).isEqualTo("REJECTED");
        assertThat(scheduling.availability(location,specialty,professional,date)).anyMatch(a -> a.startAt().equals(LocalDateTime.of(date,LocalTime.of(8,0))));
    }
    @Test void concurrentReservationsLockSharedSlotsAndReturnConflictForLoser() throws Exception {
        String suffix=UUID.randomUUID().toString();
        Long professional=scheduling.createProfessional("Concurrent","Professional","CC","CP"+suffix,"concurrent-prof-"+suffix+"@example.test","300","hash","CPC"+suffix,"CLIC"+suffix);
        Long owner=jdbc.queryForObject("select user_id from professionals where id=?",Long.class,professional);
        Long general=scheduling.createSpecialty("GEN"+suffix,"General " +suffix,30,true).id();
        Long specialized=scheduling.createSpecialty("SPC"+suffix,"Especializada " +suffix,30,false).id();
        Long location=jdbc.queryForObject("select id from locations where active=true limit 1",Long.class);
        scheduling.setProfessionalSpecialties(professional,List.of(general,specialized),general);
        scheduling.setProfessionalLocations(professional,List.of(location));
        LocalDate date=LocalDate.now().plusDays(3);
        scheduling.createBlock(owner,location,date,LocalTime.of(8,0),LocalTime.of(9,0));
        Long patientA=user("race-a-"+suffix+"@example.test","RA"+suffix);
        Long patientB=user("race-b-"+suffix+"@example.test","RB"+suffix);

        List<Attempt> generalAttempts=reserveConcurrently(patientA,patientB,professional,location,general,date,LocalTime.of(8,0));
        assertOneCreatedAndOneConflict(generalAttempts);
        assertThat(count("select count(*) from appointments a join appointment_statuses s on s.id=a.status_id where a.professional_id=? and a.specialty_id=? and a.scheduled_start_at=? and s.code='APPROVED'",professional,general,LocalDateTime.of(date,LocalTime.of(8,0)))).isEqualTo(1);
        assertSingleAppointmentOnSlots(professional,location,LocalDateTime.of(date,LocalTime.of(8,0)),LocalDateTime.of(date,LocalTime.of(8,30)));

        List<Attempt> specializedAttempts=reserveConcurrently(patientA,patientB,professional,location,specialized,date,LocalTime.of(8,30));
        assertOneCreatedAndOneConflict(specializedAttempts);
        assertThat(count("select count(*) from appointments a join appointment_statuses s on s.id=a.status_id where a.professional_id=? and a.specialty_id=? and a.scheduled_start_at=? and s.code='REQUESTED'",professional,specialized,LocalDateTime.of(date,LocalTime.of(8,30)))).isEqualTo(1);
        assertSingleAppointmentOnSlots(professional,location,LocalDateTime.of(date,LocalTime.of(8,30)),LocalDateTime.of(date,LocalTime.of(9,0)));
    }
    private record Attempt(int status,String body) {}
    private List<Attempt> reserveConcurrently(Long patientA,Long patientB,Long professional,Long location,Long specialty,LocalDate date,LocalTime time) throws Exception {
        ExecutorService pool=Executors.newFixedThreadPool(2);
        CountDownLatch ready=new CountDownLatch(2),start=new CountDownLatch(1);
        try {
            List<Future<Attempt>> futures=List.of(patientA,patientB).stream().map(patient->pool.submit(()->{
                ready.countDown(); start.await();
                String body=objectMapper.writeValueAsString(Map.of("professionalId",professional,"locationId",location,"specialtyId",specialty,"date",date.toString(),"startTime",time.toString(),"reason","Concurrency check"));
                var response=mockMvc.perform(post("/api/v1/appointments").with(SecurityMockMvcRequestPostProcessors.user(patient.toString()).roles("USER")).contentType(MediaType.APPLICATION_JSON).content(body)).andReturn().getResponse();
                return new Attempt(response.getStatus(),response.getContentAsString());
            })).toList();
            assertThat(ready.await(10,TimeUnit.SECONDS)).as("both reservation attempts are ready").isTrue();
            start.countDown();
            List<Attempt> results=List.of(futures.get(0).get(45,TimeUnit.SECONDS),futures.get(1).get(45,TimeUnit.SECONDS));
            results.forEach(result->log.info("Concurrent reservation attempt finished: HTTP {} body={}",result.status(),result.body()));
            return results;
        } finally { start.countDown(); pool.shutdownNow(); }
    }
    private void assertOneCreatedAndOneConflict(List<Attempt> attempts) {
        assertThat(attempts).extracting(Attempt::status).containsExactlyInAnyOrder(201,409);
        assertThat(attempts.stream().filter(a->a.status()==409).findFirst().orElseThrow().body()).contains("Franja ya reservada");
    }
    private void assertSingleAppointmentOnSlots(Long professional,Long location,LocalDateTime start,LocalDateTime end) {
        assertThat(count("select count(*) from professional_slots ps join availability_blocks b on b.id=ps.availability_block_id where b.professional_id=? and b.location_id=? and ps.start_at>=? and ps.end_at<=? and ps.appointment_id is not null",professional,location,start,end)).isEqualTo(1);
        assertThat(count("select count(distinct ps.appointment_id) from professional_slots ps join availability_blocks b on b.id=ps.availability_block_id where b.professional_id=? and b.location_id=? and ps.start_at>=? and ps.end_at<=? and ps.appointment_id is not null",professional,location,start,end)).isEqualTo(1);
        assertThat(count("select count(*) from appointments where professional_id=? and location_id=? and scheduled_start_at=?",professional,location,start)).isEqualTo(1);
    }
    private int count(String sql,Object... args) { Integer result=jdbc.queryForObject(sql,Integer.class,args);return result==null?0:result; }
    private Long user(String email,String document) { jdbc.update("insert into users(first_name,last_name,document_type,document_number,email,phone,password_hash,active,email_verified) values ('Test','User','CC',?,?,?,'hash',true,false)",document,email,"300"); return jdbc.queryForObject("select id from users where email=?",Long.class,email); }
}
