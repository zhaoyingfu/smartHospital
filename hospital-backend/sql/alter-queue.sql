ALTER TABLE queue
  ADD COLUMN call_count           INT       DEFAULT 0     COMMENT '呼叫次数',
  ADD COLUMN type                 VARCHAR(20) DEFAULT 'FIRST_VISIT' COMMENT 'FIRST_VISIT / REVISIT / EMERGENCY',
  ADD COLUMN revisit_source_id    BIGINT    DEFAULT NULL   COMMENT '来源queueId(复诊)',
  ADD INDEX idx_dept_doctor_status (department_id, doctor_id, status);
