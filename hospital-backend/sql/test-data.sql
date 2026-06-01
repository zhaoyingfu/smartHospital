USE smart_hospital;

-- ============================================================
-- 科室
-- ============================================================
INSERT INTO department (id, name, code, category, sort_order, status) VALUES
(1, '内科', 'D001', '内科', 1, 1),
(2, '心血管内科', 'D002', '内科', 2, 1),
(3, '消化内科', 'D003', '内科', 3, 1),
(4, '呼吸内科', 'D004', '内科', 4, 1),
(5, '外科', 'D005', '外科', 5, 1),
(6, '骨科', 'D006', '外科', 6, 1),
(7, '神经外科', 'D007', '外科', 7, 1),
(8, '妇产科', 'D008', '妇儿', 8, 1),
(9, '儿科', 'D009', '妇儿', 9, 1),
(10, '眼科', 'D010', '专科', 10, 1),
(11, '口腔科', 'D011', '专科', 11, 1),
(12, '皮肤科', 'D012', '专科', 12, 1),
(13, '中医科', 'D013', '中医', 13, 1),
(14, '康复科', 'D014', '中医', 14, 1);

-- ============================================================
-- 医生
-- ============================================================
INSERT INTO doctor (id, name, department_id, title, code) VALUES
(1, '张明', 1, '主任医师', 'DOC001'),
(2, '李丽', 1, '副主任医师', 'DOC002'),
(3, '王强', 1, '主治医师', 'DOC003'),
(4, '赵敏', 2, '主任医师', 'DOC004'),
(5, '刘洋', 2, '副主任医师', 'DOC005'),
(6, '陈伟', 3, '主任医师', 'DOC006'),
(7, '孙芳', 3, '主治医师', 'DOC007'),
(8, '周杰', 4, '副主任医师', 'DOC008'),
(9, '吴静', 5, '主任医师', 'DOC009'),
(10, '郑林', 5, '主治医师', 'DOC010'),
(11, '冯刚', 6, '主任医师', 'DOC011'),
(12, '王芳', 6, '副主任医师', 'DOC012'),
(13, '蒋平', 7, '主任医师', 'DOC013'),
(14, '沈华', 8, '主任医师', 'DOC014'),
(15, '韩雪', 8, '副主任医师', 'DOC015'),
(16, '杨明', 9, '主任医师', 'DOC016'),
(17, '朱红', 10, '主任医师', 'DOC017'),
(18, '秦亮', 11, '副主任医师', 'DOC018'),
(19, '许丽', 12, '主任医师', 'DOC019'),
(20, '何健', 13, '主任医师', 'DOC020');

-- ============================================================
-- 排班（今天的 + 未来7天的）
-- ============================================================
INSERT INTO schedule (id, doctor_id, department_id, schedule_date, time_period, total_quota, available_quota, reg_fee, treat_fee, status) VALUES
-- 内科 - 张明（主任医师）
(1, 1, 1, CURDATE(), 'AM', 30, 12, 2000, 1000, 'AVAILABLE'),
(2, 1, 1, CURDATE(), 'PM', 20, 8, 2000, 1000, 'AVAILABLE'),
(3, 1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', 30, 25, 2000, 1000, 'AVAILABLE'),
(4, 1, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AM', 30, 30, 2000, 1000, 'AVAILABLE'),
(5, 1, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'PM', 20, 20, 2000, 1000, 'AVAILABLE'),

-- 内科 - 李丽（副主任医师）
(6, 2, 1, CURDATE(), 'AM', 25, 5, 1500, 800, 'AVAILABLE'),
(7, 2, 1, CURDATE(), 'PM', 25, 15, 1500, 800, 'AVAILABLE'),
(8, 2, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', 25, 20, 1500, 800, 'AVAILABLE'),
(9, 2, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'AM', 25, 25, 1500, 800, 'AVAILABLE'),

-- 内科 - 王强（主治医师）
(10, 3, 1, CURDATE(), 'AM', 40, 3, 1000, 500, 'AVAILABLE'),
(11, 3, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', 40, 35, 1000, 500, 'AVAILABLE'),
(12, 3, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'PM', 30, 28, 1000, 500, 'AVAILABLE'),

-- 心血管内科 - 赵敏（主任医师）
(13, 4, 2, CURDATE(), 'AM', 20, 2, 2500, 1200, 'AVAILABLE'),
(14, 4, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', 20, 15, 2500, 1200, 'AVAILABLE'),
(15, 4, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'PM', 15, 12, 2500, 1200, 'AVAILABLE'),

-- 心血管内科 - 刘洋（副主任医师）
(16, 5, 2, CURDATE(), 'AM', 25, 10, 1500, 800, 'AVAILABLE'),
(17, 5, 2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AM', 25, 22, 1500, 800, 'AVAILABLE'),

-- 消化内科 - 陈伟（主任医师）
(18, 6, 3, CURDATE(), 'AM', 20, 6, 2000, 1000, 'AVAILABLE'),
(19, 6, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', 20, 18, 2000, 1000, 'AVAILABLE'),

-- 消化内科 - 孙芳（主治医师）
(20, 7, 3, CURDATE(), 'PM', 30, 18, 1000, 500, 'AVAILABLE'),
(21, 7, 3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AM', 30, 28, 1000, 500, 'AVAILABLE'),

-- 外科 - 吴静（主任医师）
(22, 9, 5, CURDATE(), 'AM', 15, 4, 2500, 1200, 'AVAILABLE'),
(23, 9, 5, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', 15, 12, 2500, 1200, 'AVAILABLE'),

-- 外科 - 郑林（主治医师）
(24, 10, 5, CURDATE(), 'PM', 30, 20, 1000, 500, 'AVAILABLE'),
(25, 10, 5, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'PM', 30, 30, 1000, 500, 'AVAILABLE'),

-- 骨科 - 冯刚（主任医师）
(26, 11, 6, CURDATE(), 'AM', 20, 8, 2500, 1200, 'AVAILABLE'),
(27, 11, 6, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', 20, 16, 2500, 1200, 'AVAILABLE'),

-- 骨科 - 王芳（副主任医师）
(28, 12, 6, CURDATE(), 'PM', 25, 14, 1500, 800, 'AVAILABLE'),

-- 儿科 - 杨明（主任医师）
(29, 16, 9, CURDATE(), 'AM', 30, 10, 2000, 1000, 'AVAILABLE'),
(30, 16, 9, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', 30, 22, 2000, 1000, 'AVAILABLE'),

-- 中医科 - 何健（主任医师）
(31, 20, 13, CURDATE(), 'AM', 20, 5, 2000, 800, 'AVAILABLE'),
(32, 20, 13, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', 20, 15, 2000, 800, 'AVAILABLE');

-- ============================================================
-- 患者
-- ============================================================
INSERT INTO patient (id, name, id_card, medicare_card, phone, medical_no, is_blacklist) VALUES
(1, '张三', '110101199001011234', 'YB20260001', '13800138000', 'MZ20260529001', 0),
(2, '李四', '110101198505152345', 'YB20260002', '13900139000', 'MZ20260529002', 0),
(3, '王五', '110101197503036789', 'YB20260003', '13700137000', 'MZ20260529003', 0),
(4, '赵六', '110101196808084567', 'YB20260004', '13600136000', 'MZ20260529004', 1),
(5, '钱七', '110101199212129012', 'YB20260005', '13500135000', 'MZ20260529005', 0);

-- ============================================================
-- 黑名单
-- ============================================================
INSERT INTO patient_blacklist (id, patient_id, reason, operator_id) VALUES
(1, 4, '恶意挂号和多次爽约', 1);

-- ============================================================
-- 挂号记录（今天的 + 前几天的历史）
-- ============================================================
INSERT INTO registration (id, reg_no, patient_id, schedule_id, doctor_id, department_id, reg_date, time_period, reg_fee, treat_fee, total_fee, reg_type, status, sign_time, queue_no) VALUES
-- 张三 - 今天挂了内科张明上午的号
(1, 'RG202605290001', 1, 1, 1, 1, CURDATE(), 'AM', 2000, 1000, 3000, 'SAME_DAY', 'REGISTERED', NULL, NULL),

-- 李四 - 今天挂了心血管内科赵敏上午的号（已签到）
(2, 'RG202605290002', 2, 13, 4, 2, CURDATE(), 'AM', 2500, 1200, 3700, 'SAME_DAY', 'SIGNED_IN', NOW(), 'A003'),

-- 王五 - 预约明天的内科李丽
(3, 'RG202605290003', 3, 8, 2, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', 1500, 800, 2300, 'APPOINTMENT', 'REGISTERED', NULL, NULL),

-- 张三 - 昨天的历史挂号记录（已就诊）
(4, 'RG202605280001', 1, 10, 3, 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'AM', 1000, 500, 1500, 'SAME_DAY', 'SEEN', DATE_SUB(NOW(), INTERVAL 1 DAY), 'A005'),

-- 李四 - 前天的历史挂号记录（已退号）
(5, 'RG202605270001', 2, 18, 6, 3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'AM', 2000, 1000, 3000, 'SAME_DAY', 'REFUNDED', NULL, NULL),

-- 钱七 - 今天的记录
(6, 'RG202605290004', 5, 26, 11, 6, CURDATE(), 'AM', 2500, 1200, 3700, 'SAME_DAY', 'REGISTERED', NULL, NULL);

-- ============================================================
-- 排队记录
-- ============================================================
INSERT INTO queue (id, registration_id, department_id, doctor_id, queue_no, status, call_time, wait_count) VALUES
(1, 2, 2, 4, 'A003', 'SEEING', NOW(), 3),
(2, 1, 1, 1, 'A001', 'WAITING', NULL, 5);

-- ============================================================
-- 药品信息
-- ============================================================
INSERT INTO drug_info (id, name, pin_yin, spec, unit_price, category, status) VALUES
(1, '阿莫西林胶囊', 'AMXLJN', '0.5g*24粒/盒', 1280, '西药', 1),
(2, '布洛芬缓释胶囊', 'BLFHSJN', '0.3g*20粒/盒', 1560, '西药', 1),
(3, '头孢克肟片', 'TBKWP', '50mg*12片/盒', 2250, '西药', 1),
(4, '奥美拉唑胶囊', 'AMLZJN', '20mg*14粒/盒', 1850, '西药', 1),
(5, '硝苯地平缓释片', 'XBDPHSP', '30mg*30片/盒', 2850, '西药', 1),
(6, '阿托伐他汀钙片', 'ATFTTGP', '20mg*7片/盒', 4200, '西药', 1),
(7, '氯雷他定片', 'LLTDP', '10mg*12片/盒', 1280, '西药', 1),
(8, '连花清瘟胶囊', 'LHQWJN', '0.35g*36粒/盒', 1480, '中成药', 1),
(9, '板蓝根颗粒', 'BLGKL', '10g*20袋/包', 980, '中成药', 1),
(10, '六味地黄丸', 'LWDHW', '360丸/瓶', 2680, '中成药', 1),
(11, '阿胶', 'EJ', '250g/盒', 12800, '中成药', 1),
(12, '维生素C片', 'WSSCP', '100片/瓶', 380, '西药', 1);

-- ============================================================
-- 门诊缴费单和明细
-- ============================================================
INSERT INTO outpatient_order (id, order_no, patient_id, visit_no, total_amount, status) VALUES
(1, 'OD20260529001', 1, 'VZ20260529001', 25600, 'UNPAID'),
(2, 'OD20260529002', 2, 'VZ20260529002', 8900, 'UNPAID');

INSERT INTO outpatient_order_item (id, outpatient_order_id, item_name, item_code, quantity, unit_price, amount) VALUES
(1, 1, '血常规检查', 'LAB001', 1, 1500, 1500),
(2, 1, '肝功能检查', 'LAB002', 1, 8000, 8000),
(3, 1, 'CT检查（胸部）', 'IMG001', 1, 16100, 16100),
(4, 2, '尿常规检查', 'LAB003', 1, 800, 800),
(5, 2, '心电图检查', 'LAB004', 1, 2500, 2500),
(6, 2, 'X光检查（胸部）', 'IMG002', 1, 5600, 5600);

-- ============================================================
-- 支付订单和明细
-- ============================================================
INSERT INTO payment_order (id, out_trade_no, biz_type, biz_id, biz_no, total_amount, insurance_amount, self_amount, status, expire_time, kiosk_id) VALUES
(1, 'PAY202605290001', 'REGISTRATION', 1, 'RG202605290001', 3000, 0, 3000, 'PENDING', DATE_ADD(NOW(), INTERVAL 5 MINUTE), 1),
(2, 'PAY202605290002', 'REGISTRATION', 2, 'RG202605290002', 3700, 1500, 2200, 'SUCCESS', NOW(), 1),
(3, 'PAY202605290003', 'REGISTRATION', 3, 'RG202605290003', 2300, 0, 2300, 'PENDING', DATE_ADD(NOW(), INTERVAL 5 MINUTE), 1),
(4, 'PAY202605290004', 'REGISTRATION', 6, 'RG202605290004', 3700, 0, 3700, 'PENDING', DATE_ADD(NOW(), INTERVAL 5 MINUTE), 2),
(5, 'PAY202605290005', 'OUTPATIENT', 1, 'OD20260529001', 25600, 8000, 17600, 'PENDING', DATE_ADD(NOW(), INTERVAL 5 MINUTE), 1),
(6, 'PAY202605290006', 'OUTPATIENT', 2, 'OD20260529002', 8900, 0, 8900, 'PENDING', DATE_ADD(NOW(), INTERVAL 5 MINUTE), 1);

INSERT INTO payment_detail (id, payment_order_id, pay_method, amount, trade_no, status, pay_time, callback_time) VALUES
(1, 2, 'MEDICARE', 1500, 'MED202605290001', 'SUCCESS', NOW(), NOW()),
(2, 2, 'WECHAT', 2200, 'WX202605290001', 'SUCCESS', NOW(), NOW());

-- ============================================================
-- 自助机设备
-- ============================================================
INSERT INTO kiosk_machine (id, machine_no, location, status, last_heartbeat, paper_status, app_version, features) VALUES
(1, 'KS001', '门诊大厅1楼-1号机', 'ONLINE', NOW(), 'NORMAL', '1.0.0', '{"registration":true,"payment":true,"query":true,"signin":true}'),
(2, 'KS002', '门诊大厅1楼-2号机', 'ONLINE', NOW(), 'NORMAL', '1.0.0', '{"registration":true,"payment":true,"query":true,"signin":true}'),
(3, 'KS003', '门诊大厅2楼-3号机', 'ONLINE', DATE_SUB(NOW(), INTERVAL 1 MINUTE), 'LOW', '1.0.0', '{"registration":true,"payment":true,"query":true,"signin":true}'),
(4, 'KS004', '住院部1楼-4号机', 'OFFLINE', DATE_SUB(NOW(), INTERVAL 1 HOUR), 'EMPTY', '1.0.0', '{"registration":false,"payment":true,"query":true,"signin":false}'),
(5, 'KS005', '急诊大厅-5号机', 'MAINTENANCE', DATE_SUB(NOW(), INTERVAL 30 MINUTE), 'NORMAL', '1.0.0', '{"registration":true,"payment":true,"query":true,"signin":true}');

-- ============================================================
-- 心跳日志
-- ============================================================
INSERT INTO kiosk_heartbeat (id, kiosk_id, heartbeat_time, paper_remaining) VALUES
(1, 1, NOW(), 80),
(2, 2, NOW(), 65),
(3, 3, DATE_SUB(NOW(), INTERVAL 1 MINUTE), 15);
