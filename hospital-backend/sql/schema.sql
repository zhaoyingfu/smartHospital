CREATE DATABASE IF NOT EXISTS smart_hospital DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_hospital;

-- ============================================================
-- 患者与身份
-- ============================================================

CREATE TABLE patient (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    id_card VARCHAR(128) NOT NULL COMMENT '身份证号(AES加密存储)',
    medicare_card VARCHAR(50) DEFAULT NULL COMMENT '医保卡号',
    phone VARCHAR(128) DEFAULT NULL COMMENT '手机号(加密)',
    medical_no VARCHAR(30) NOT NULL COMMENT '就诊卡号',
    is_blacklist TINYINT(1) NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_medical_no (medical_no),
    KEY idx_id_card (id_card),
    KEY idx_phone (phone)
) ENGINE=InnoDB COMMENT='患者信息';

CREATE TABLE patient_blacklist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    reason VARCHAR(500) NOT NULL COMMENT '拉黑原因',
    operator_id BIGINT DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_patient_id (patient_id)
) ENGINE=InnoDB COMMENT='患者黑名单';

-- ============================================================
-- 排班与号源
-- ============================================================

CREATE TABLE department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(30) NOT NULL COMMENT 'HIS科室编码',
    category VARCHAR(30) NOT NULL COMMENT '内科/外科/妇儿/...',
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
    his_sync_time DATETIME DEFAULT NULL,
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB COMMENT='科室';

CREATE TABLE doctor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    department_id BIGINT NOT NULL,
    title VARCHAR(30) NOT NULL COMMENT '职称',
    code VARCHAR(30) NOT NULL COMMENT 'HIS医生编码',
    status TINYINT NOT NULL DEFAULT 1,
    his_sync_time DATETIME DEFAULT NULL,
    KEY idx_department_id (department_id),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB COMMENT='医生';

CREATE TABLE schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    schedule_date DATE NOT NULL,
    time_period VARCHAR(10) NOT NULL COMMENT 'AM/PM/EVENING',
    total_quota INT NOT NULL DEFAULT 0,
    available_quota INT NOT NULL DEFAULT 0,
    reg_fee INT NOT NULL DEFAULT 0 COMMENT '挂号费(分)',
    treat_fee INT NOT NULL DEFAULT 0 COMMENT '诊查费(分)',
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT 'AVAILABLE/DISABLED/FULL',
    his_sync_time DATETIME DEFAULT NULL,
    KEY idx_doctor_date (doctor_id, schedule_date),
    KEY idx_dept_date (department_id, schedule_date)
) ENGINE=InnoDB COMMENT='排班号源';

CREATE TABLE schedule_lock (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    schedule_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    lock_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expire_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'LOCKED' COMMENT 'LOCKED/RELEASED/CONVERTED',
    KEY idx_schedule_id (schedule_id),
    KEY idx_expire_time (expire_time)
) ENGINE=InnoDB COMMENT='号源锁定记录';

-- ============================================================
-- 业务单据
-- ============================================================

CREATE TABLE registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reg_no VARCHAR(30) NOT NULL COMMENT '挂号流水号',
    patient_id BIGINT NOT NULL,
    schedule_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    reg_date DATE NOT NULL,
    time_period VARCHAR(10) NOT NULL,
    reg_fee INT NOT NULL DEFAULT 0 COMMENT '挂号费(分)',
    treat_fee INT NOT NULL DEFAULT 0 COMMENT '诊查费(分)',
    total_fee INT NOT NULL DEFAULT 0 COMMENT '合计(分)',
    reg_type VARCHAR(20) NOT NULL COMMENT 'SAME_DAY/APPOINTMENT',
    status VARCHAR(20) NOT NULL DEFAULT 'REGISTERED' COMMENT 'REGISTERED/SIGNED_IN/SEEN/REFUNDED',
    sign_time DATETIME DEFAULT NULL,
    queue_no VARCHAR(10) DEFAULT NULL,
    his_order_no VARCHAR(50) DEFAULT NULL,
    kiosk_id BIGINT DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_reg_no (reg_no),
    KEY idx_patient_id (patient_id),
    KEY idx_schedule_id (schedule_id)
) ENGINE=InnoDB COMMENT='挂号记录';

CREATE TABLE queue (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    registration_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    queue_no VARCHAR(10) NOT NULL COMMENT '排队号(A001)',
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING' COMMENT 'WAITING/SEEING/SKIPPED/DONE',
    call_time DATETIME DEFAULT NULL,
    wait_count INT DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_department_doctor (department_id, doctor_id),
    KEY idx_registration_id (registration_id)
) ENGINE=InnoDB COMMENT='排队记录';

CREATE TABLE outpatient_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(30) NOT NULL COMMENT '门诊缴费单号',
    patient_id BIGINT NOT NULL,
    visit_no VARCHAR(30) NOT NULL COMMENT '就诊号',
    total_amount INT NOT NULL DEFAULT 0 COMMENT '总金额(分)',
    status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' COMMENT 'UNPAID/PAID/REFUNDED',
    his_order_no VARCHAR(50) DEFAULT NULL,
    kiosk_id BIGINT DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_patient_id (patient_id)
) ENGINE=InnoDB COMMENT='门诊缴费单';

CREATE TABLE outpatient_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    outpatient_order_id BIGINT NOT NULL,
    item_name VARCHAR(200) NOT NULL,
    item_code VARCHAR(30) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price INT NOT NULL DEFAULT 0 COMMENT '单价(分)',
    amount INT NOT NULL DEFAULT 0 COMMENT '小计(分)',
    his_item_code VARCHAR(30) DEFAULT NULL,
    KEY idx_order_id (outpatient_order_id)
) ENGINE=InnoDB COMMENT='门诊缴费明细';

CREATE TABLE inpatient_deposit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    deposit_no VARCHAR(30) NOT NULL COMMENT '充值流水号',
    patient_id BIGINT NOT NULL,
    inpatient_no VARCHAR(30) NOT NULL COMMENT '住院号',
    amount INT NOT NULL DEFAULT 0 COMMENT '充值金额(分)',
    balance_before INT NOT NULL DEFAULT 0 COMMENT '充值前余额(分)',
    balance_after INT NOT NULL DEFAULT 0 COMMENT '充值后余额(分)',
    status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' COMMENT 'UNPAID/PAID/REFUNDED',
    his_receipt_no VARCHAR(50) DEFAULT NULL,
    kiosk_id BIGINT DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_deposit_no (deposit_no),
    KEY idx_patient_id (patient_id)
) ENGINE=InnoDB COMMENT='住院预交金充值';

CREATE TABLE drug_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    pin_yin VARCHAR(100) NOT NULL COMMENT '拼音码',
    spec VARCHAR(100) DEFAULT NULL COMMENT '规格',
    unit_price INT NOT NULL DEFAULT 0 COMMENT '单价(分)',
    category VARCHAR(20) NOT NULL COMMENT '西药/中成药/中草药',
    status TINYINT NOT NULL DEFAULT 1,
    his_sync_time DATETIME DEFAULT NULL,
    KEY idx_pin_yin (pin_yin),
    KEY idx_name (name)
) ENGINE=InnoDB COMMENT='药品信息';

-- ============================================================
-- 支付与对账
-- ============================================================

CREATE TABLE payment_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    out_trade_no VARCHAR(40) NOT NULL COMMENT '商户订单号',
    biz_type VARCHAR(20) NOT NULL COMMENT 'REGISTRATION/OUTPATIENT/INPATIENT',
    biz_id BIGINT NOT NULL,
    biz_no VARCHAR(30) NOT NULL COMMENT '业务单号',
    total_amount INT NOT NULL DEFAULT 0 COMMENT '总金额(分)',
    insurance_amount INT NOT NULL DEFAULT 0 COMMENT '医保支付金额(分)',
    self_amount INT NOT NULL DEFAULT 0 COMMENT '自费金额(分)',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/PROCESSING/SUCCESS/FAILED/CLOSED/REFUNDED/PARTIAL_REFUND',
    expire_time DATETIME NOT NULL,
    kiosk_id BIGINT DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_out_trade_no (out_trade_no),
    KEY idx_biz (biz_type, biz_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB COMMENT='支付订单';

CREATE TABLE payment_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_order_id BIGINT NOT NULL,
    pay_method VARCHAR(20) NOT NULL COMMENT 'WECHAT/ALIPAY/BANK_CARD/MEDICARE',
    amount INT NOT NULL DEFAULT 0 COMMENT '该方式支付金额(分)',
    trade_no VARCHAR(64) DEFAULT NULL COMMENT '第三方交易流水号',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/SUCCESS/FAILED',
    fail_reason VARCHAR(500) DEFAULT NULL,
    pay_time DATETIME DEFAULT NULL,
    callback_time DATETIME DEFAULT NULL,
    KEY idx_payment_order_id (payment_order_id),
    KEY idx_trade_no (trade_no)
) ENGINE=InnoDB COMMENT='支付明细';

CREATE TABLE reconciliation_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recon_date DATE NOT NULL COMMENT '对账日期',
    channel VARCHAR(20) NOT NULL COMMENT 'WECHAT/ALIPAY/BANK/HIS',
    total_count INT NOT NULL DEFAULT 0,
    matched_count INT NOT NULL DEFAULT 0,
    long_count INT NOT NULL DEFAULT 0 COMMENT '渠有系无',
    short_count INT NOT NULL DEFAULT 0 COMMENT '系有渠无',
    amount_diff_count INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PROCESSING' COMMENT 'PROCESSING/DONE/HAS_DIFF',
    recon_file_url VARCHAR(500) DEFAULT NULL,
    start_time DATETIME DEFAULT NULL,
    end_time DATETIME DEFAULT NULL,
    UNIQUE KEY uk_date_channel (recon_date, channel)
) ENGINE=InnoDB COMMENT='对账记录';

CREATE TABLE reconciliation_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reconciliation_record_id BIGINT NOT NULL,
    result VARCHAR(30) NOT NULL COMMENT 'MATCHED/SYSTEM_ONLY/CHANNEL_ONLY/AMOUNT_MISMATCH',
    system_trade_no VARCHAR(40) DEFAULT NULL,
    system_amount INT DEFAULT NULL,
    channel_trade_no VARCHAR(64) DEFAULT NULL,
    channel_amount INT DEFAULT NULL,
    diff_amount INT DEFAULT NULL,
    handle_status VARCHAR(20) NOT NULL DEFAULT 'UNHANDLED' COMMENT 'UNHANDLED/HANDLED',
    handle_result VARCHAR(20) DEFAULT NULL COMMENT 'ADJUST/PENDING/EXEMPT',
    handler_id BIGINT DEFAULT NULL,
    handle_time DATETIME DEFAULT NULL,
    KEY idx_record_id (reconciliation_record_id),
    KEY idx_handle_status (handle_status)
) ENGINE=InnoDB COMMENT='对账明细';

CREATE TABLE reconciliation_ticket (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reconciliation_detail_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(1000) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN/IN_PROGRESS/CLOSED',
    assignee_id BIGINT DEFAULT NULL,
    resolution VARCHAR(1000) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_detail_id (reconciliation_detail_id)
) ENGINE=InnoDB COMMENT='对账差异工单';

CREATE TABLE refund_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    refund_no VARCHAR(30) NOT NULL,
    payment_order_id BIGINT NOT NULL,
    refund_amount INT NOT NULL DEFAULT 0 COMMENT '退款金额(分)',
    refund_reason VARCHAR(500) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_APPROVE' COMMENT 'PENDING_APPROVE/APPROVED/REFUNDING/REFUNDED/FAILED',
    applicant_id BIGINT DEFAULT NULL,
    approver_id BIGINT DEFAULT NULL,
    approve_time DATETIME DEFAULT NULL,
    refund_time DATETIME DEFAULT NULL,
    biz_type VARCHAR(20) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_refund_no (refund_no),
    KEY idx_payment_order_id (payment_order_id)
) ENGINE=InnoDB COMMENT='退款单';

CREATE TABLE daily_settlement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    settle_date DATE NOT NULL,
    total_transaction INT NOT NULL DEFAULT 0,
    total_amount INT NOT NULL DEFAULT 0,
    wechat_amount INT NOT NULL DEFAULT 0,
    alipay_amount INT NOT NULL DEFAULT 0,
    bank_amount INT NOT NULL DEFAULT 0,
    medicare_amount INT NOT NULL DEFAULT 0,
    total_refund INT NOT NULL DEFAULT 0,
    recon_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/RECON_DONE/HAS_DIFF',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/CONFIRMED/LOCKED',
    confirmer_id BIGINT DEFAULT NULL,
    confirm_time DATETIME DEFAULT NULL,
    UNIQUE KEY uk_settle_date (settle_date)
) ENGINE=InnoDB COMMENT='日终结算';

-- ============================================================
-- 设备与规则
-- ============================================================

CREATE TABLE kiosk_machine (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    machine_no VARCHAR(30) NOT NULL COMMENT '设备编号',
    location VARCHAR(200) DEFAULT NULL COMMENT '安装位置',
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE' COMMENT 'ONLINE/OFFLINE/MAINTENANCE',
    last_heartbeat DATETIME DEFAULT NULL,
    paper_status VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT 'NORMAL/LOW/EMPTY',
    app_version VARCHAR(20) DEFAULT NULL,
    features JSON DEFAULT NULL COMMENT '启用的功能配置',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_machine_no (machine_no)
) ENGINE=InnoDB COMMENT='自助机设备';

CREATE TABLE kiosk_heartbeat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    kiosk_id BIGINT NOT NULL,
    heartbeat_time DATETIME NOT NULL,
    paper_remaining INT DEFAULT NULL COMMENT '剩余纸张百分比',
    KEY idx_kiosk_id (kiosk_id),
    KEY idx_heartbeat_time (heartbeat_time)
) ENGINE=InnoDB COMMENT='心跳日志';

CREATE TABLE biz_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_key VARCHAR(50) NOT NULL COMMENT 'LOCK_DURATION/DAILY_REG_LIMIT/...',
    rule_value VARCHAR(200) NOT NULL,
    description VARCHAR(200) DEFAULT NULL,
    scope VARCHAR(20) NOT NULL DEFAULT 'GLOBAL' COMMENT 'GLOBAL/MACHINE',
    scope_id BIGINT DEFAULT NULL,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_key_scope (rule_key, scope, scope_id)
) ENGINE=InnoDB COMMENT='业务规则配置';

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(128) NOT NULL COMMENT 'BCrypt加密',
    real_name VARCHAR(50) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    role VARCHAR(20) NOT NULL COMMENT 'SUPER_ADMIN/FINANCE/OPS',
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB COMMENT='管理员';

-- 初始管理员 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, role)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 'SUPER_ADMIN');

-- 默认业务规则
INSERT INTO biz_rule (rule_key, rule_value, description, scope) VALUES
('LOCK_DURATION', '300', '号源锁定时长(秒)', 'GLOBAL'),
('PAY_TIMEOUT', '300', '支付超时时间(秒)', 'GLOBAL'),
('REFUND_APPROVE_THRESHOLD', '20000', '需审批退款金额阈值(分)', 'GLOBAL'),
('IDLE_TIMEOUT', '60', '自助机无操作超时回首页(秒)', 'GLOBAL'),
('DAILY_REG_LIMIT', '3', '单患者单日挂号上限', 'GLOBAL');
