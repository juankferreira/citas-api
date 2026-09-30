-- Datos sintéticos reproducibles para la demostración académica.
-- Contraseña de las tres cuentas: DemoCitas2026!

INSERT INTO users(first_name,last_name,document_type,document_number,email,phone,password_hash,active,email_verified)
SELECT 'Paciente','Demo','CC','90000001','paciente.demo@lab.local','3000000001','$2a$10$Z1eY4ceXPLjg4hG3HMJV3uNe3CxZsrq.SJLMEbTQaDPLEAeUGYO52',TRUE,TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='paciente.demo@lab.local');
INSERT INTO users(first_name,last_name,document_type,document_number,email,phone,password_hash,active,email_verified)
SELECT 'Profesional','Demo','CC','90000002','profesional.demo@lab.local','3000000002','$2a$10$Z1eY4ceXPLjg4hG3HMJV3uNe3CxZsrq.SJLMEbTQaDPLEAeUGYO52',TRUE,TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='profesional.demo@lab.local');
INSERT INTO users(first_name,last_name,document_type,document_number,email,phone,password_hash,active,email_verified)
SELECT 'Administrador','Demo','CC','90000003','admin.demo@lab.local','3000000003','$2a$10$Z1eY4ceXPLjg4hG3HMJV3uNe3CxZsrq.SJLMEbTQaDPLEAeUGYO52',TRUE,TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='admin.demo@lab.local');

INSERT IGNORE INTO user_roles(user_id,role_id) SELECT u.id,r.id FROM users u JOIN roles r ON r.code='USER' WHERE u.email IN ('paciente.demo@lab.local','profesional.demo@lab.local','admin.demo@lab.local');
INSERT IGNORE INTO user_roles(user_id,role_id) SELECT u.id,r.id FROM users u JOIN roles r ON r.code='PROFESSIONAL' WHERE u.email='profesional.demo@lab.local';
INSERT IGNORE INTO user_roles(user_id,role_id) SELECT u.id,r.id FROM users u JOIN roles r ON r.code='ADMIN' WHERE u.email='admin.demo@lab.local';

INSERT INTO professionals(user_id,professional_code,license_number,active)
SELECT u.id,'PROF-LAB-001','LIC-LAB-001',TRUE FROM users u
WHERE u.email='profesional.demo@lab.local' AND NOT EXISTS (SELECT 1 FROM professionals WHERE professional_code='PROF-LAB-001');
INSERT IGNORE INTO professional_specialties(professional_id,specialty_id,is_primary,active)
SELECT p.id,s.id,TRUE,TRUE FROM professionals p JOIN specialties s ON s.code='MEDICINA_GENERAL' WHERE p.professional_code='PROF-LAB-001';
INSERT IGNORE INTO professional_locations(professional_id,location_id,active)
SELECT p.id,l.id,TRUE FROM professionals p JOIN locations l ON l.code='HIC' WHERE p.professional_code='PROF-LAB-001';

INSERT INTO availability_blocks(professional_id,location_id,available_date,start_time,end_time,active)
SELECT p.id,l.id,DATE_ADD(CURDATE(),INTERVAL 1 DAY),'08:00:00','10:00:00',TRUE
FROM professionals p JOIN locations l ON l.code='HIC'
WHERE p.professional_code='PROF-LAB-001' AND NOT EXISTS (
  SELECT 1 FROM availability_blocks b WHERE b.professional_id=p.id AND b.location_id=l.id
  AND b.available_date=DATE_ADD(CURDATE(),INTERVAL 1 DAY) AND b.start_time='08:00:00'
);

INSERT IGNORE INTO professional_slots(availability_block_id,start_at,end_at)
SELECT b.id,TIMESTAMP(b.available_date,'08:00:00'),TIMESTAMP(b.available_date,'08:30:00') FROM availability_blocks b JOIN professionals p ON p.id=b.professional_id WHERE p.professional_code='PROF-LAB-001' AND b.available_date=DATE_ADD(CURDATE(),INTERVAL 1 DAY) AND b.start_time='08:00:00';
INSERT IGNORE INTO professional_slots(availability_block_id,start_at,end_at)
SELECT b.id,TIMESTAMP(b.available_date,'08:30:00'),TIMESTAMP(b.available_date,'09:00:00') FROM availability_blocks b JOIN professionals p ON p.id=b.professional_id WHERE p.professional_code='PROF-LAB-001' AND b.available_date=DATE_ADD(CURDATE(),INTERVAL 1 DAY) AND b.start_time='08:00:00';
INSERT IGNORE INTO professional_slots(availability_block_id,start_at,end_at)
SELECT b.id,TIMESTAMP(b.available_date,'09:00:00'),TIMESTAMP(b.available_date,'09:30:00') FROM availability_blocks b JOIN professionals p ON p.id=b.professional_id WHERE p.professional_code='PROF-LAB-001' AND b.available_date=DATE_ADD(CURDATE(),INTERVAL 1 DAY) AND b.start_time='08:00:00';
INSERT IGNORE INTO professional_slots(availability_block_id,start_at,end_at)
SELECT b.id,TIMESTAMP(b.available_date,'09:30:00'),TIMESTAMP(b.available_date,'10:00:00') FROM availability_blocks b JOIN professionals p ON p.id=b.professional_id WHERE p.professional_code='PROF-LAB-001' AND b.available_date=DATE_ADD(CURDATE(),INTERVAL 1 DAY) AND b.start_time='08:00:00';

INSERT INTO appointments(patient_user_id,professional_id,location_id,specialty_id,status_id,reason,scheduled_start_at,scheduled_end_at,created_by_user_id,approved_by_user_id,approved_at)
SELECT patient.id,p.id,l.id,s.id,st.id,'Cita sintética para demostración del curso',TIMESTAMP(DATE_ADD(CURDATE(),INTERVAL 1 DAY),'08:00:00'),TIMESTAMP(DATE_ADD(CURDATE(),INTERVAL 1 DAY),'08:30:00'),patient.id,admin.id,NOW()
FROM users patient JOIN users admin ON admin.email='admin.demo@lab.local' JOIN professionals p ON p.professional_code='PROF-LAB-001' JOIN locations l ON l.code='HIC' JOIN specialties s ON s.code='MEDICINA_GENERAL' JOIN appointment_statuses st ON st.code='APPROVED'
WHERE patient.email='paciente.demo@lab.local' AND NOT EXISTS (
  SELECT 1 FROM appointments a WHERE a.patient_user_id=patient.id AND a.professional_id=p.id AND a.scheduled_start_at=TIMESTAMP(DATE_ADD(CURDATE(),INTERVAL 1 DAY),'08:00:00')
);

UPDATE professional_slots ps JOIN appointments a ON a.scheduled_start_at=ps.start_at JOIN professionals p ON p.id=a.professional_id
SET ps.appointment_id=a.id
WHERE p.professional_code='PROF-LAB-001' AND ps.appointment_id IS NULL AND a.reason='Cita sintética para demostración del curso';
INSERT INTO appointment_status_history(appointment_id,status_id,changed_by_user_id,change_source,reason)
SELECT a.id,st.id,admin.id,'ADMIN','Aprobación de dato sintético de laboratorio'
FROM appointments a JOIN appointment_statuses st ON st.code='APPROVED' JOIN users admin ON admin.email='admin.demo@lab.local'
WHERE a.reason='Cita sintética para demostración del curso' AND NOT EXISTS (
  SELECT 1 FROM appointment_status_history h WHERE h.appointment_id=a.id AND h.status_id=st.id
);
