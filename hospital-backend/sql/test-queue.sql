USE smart_hospital;

-- 1. 给 queue 表加新字段（如果还没加）
ALTER TABLE queue
  ADD COLUMN call_count           INT       DEFAULT 0     COMMENT '呼叫次数',
  ADD COLUMN type                 VARCHAR(20) DEFAULT 'FIRST_VISIT' COMMENT 'FIRST_VISIT / REVISIT / EMERGENCY',
  ADD COLUMN revisit_source_id    BIGINT    DEFAULT NULL   COMMENT '来源queueId(复诊)',
  ADD INDEX idx_dept_doctor_status (department_id, doctor_id, status);

-- 2. 清空已有排队记录（避免脏数据），重新插入干净的
DELETE FROM queue;

-- 张三 已挂号(REGISTERED) 未签到 — 用来测试签到
INSERT INTO registration (id, reg_no, patient_id, schedule_id, doctor_id, department_id, reg_date, time_period, reg_fee, treat_fee, total_fee, reg_type, status)
VALUES (10, 'RG20260603001', 1, 1, 1, 1, CURDATE(), 'AM', 2000, 1000, 3000, 'SAME_DAY', 'REGISTERED');

-- 钱七 已挂号(REGISTERED) 未签到 — 另一个测试
INSERT INTO registration (id, reg_no, patient_id, schedule_id, doctor_id, department_id, reg_date, time_period, reg_fee, treat_fee, total_fee, reg_type, status)
VALUES (11, 'RG20260603002', 5, 26, 11, 6, CURDATE(), 'AM', 2500, 1200, 3700, 'SAME_DAY', 'REGISTERED');
