package co.com.fcv.training.citas.adapter.web;

import co.com.fcv.training.citas.application.SchedulingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/integrations/n8n")
class N8nIntegrationController {
    private final SchedulingService scheduling;
    private final String integrationKey;

    N8nIntegrationController(SchedulingService scheduling,
                             @Value("${app.n8n.integration-api-key:}") String integrationKey) {
        this.scheduling = scheduling;
        this.integrationKey = integrationKey;
    }

    @GetMapping("/appointments/upcoming")
    List<Map<String, Object>> upcoming(@RequestHeader("X-N8N-Integration-Key") String key,
                                        @RequestParam LocalDate from, @RequestParam LocalDate to) {
        authorize(key);
        return scheduling.upcoming(from, to, null);
    }

    @GetMapping("/appointments/{id}/recipient")
    Map<String, Object> recipient(@RequestHeader("X-N8N-Integration-Key") String key, @PathVariable Long id) {
        authorize(key);
        return scheduling.notificationRecipient(id);
    }

    @GetMapping("/daily-summary")
    List<Map<String, Object>> dailySummary(@RequestHeader("X-N8N-Integration-Key") String key,
                                            @RequestParam LocalDate date) {
        authorize(key);
        return scheduling.dailySummary(date);
    }

    private void authorize(String key) {
        if (integrationKey.isBlank() || !MessageDigest.isEqual(integrationKey.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Clave de integración inválida");
        }
    }
}
