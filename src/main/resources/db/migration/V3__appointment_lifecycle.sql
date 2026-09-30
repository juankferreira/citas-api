CREATE TABLE reschedule_statuses (
  id SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(40) NOT NULL UNIQUE,
  name VARCHAR(80) NOT NULL,
  is_terminal BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB;
CREATE TABLE reschedule_requests (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  appointment_id BIGINT UNSIGNED NOT NULL,
  requested_by_user_id BIGINT UNSIGNED NOT NULL,
  status_id SMALLINT UNSIGNED NOT NULL,
  requested_start_at DATETIME NOT NULL,
  requested_end_at DATETIME NOT NULL,
  decision_reason VARCHAR(500) NULL,
  decided_by_user_id BIGINT UNSIGNED NULL,
  decided_at DATETIME NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (appointment_id) REFERENCES appointments(id),
  FOREIGN KEY (requested_by_user_id) REFERENCES users(id),
  FOREIGN KEY (status_id) REFERENCES reschedule_statuses(id),
  FOREIGN KEY (decided_by_user_id) REFERENCES users(id),
  UNIQUE KEY uq_reschedule_pending (appointment_id, status_id),
  INDEX ix_reschedule_status (status_id, requested_start_at)
) ENGINE=InnoDB;
ALTER TABLE professional_slots ADD COLUMN reschedule_request_id BIGINT UNSIGNED NULL,
  ADD CONSTRAINT fk_slots_reschedule FOREIGN KEY (reschedule_request_id) REFERENCES reschedule_requests(id) ON DELETE SET NULL;
INSERT INTO reschedule_statuses(code,name,is_terminal) VALUES
 ('PENDING','Pendiente',FALSE),('APPROVED','Aprobada',TRUE),('REJECTED','Rechazada',TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name),is_terminal=VALUES(is_terminal);
