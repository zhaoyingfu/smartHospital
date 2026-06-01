# 智慧医院自助服务系统 (smartHospital)

基于 **Vue3 + Spring Boot 3** 的医院自助服务终端系统，包含自助机端（Kiosk）和管理后台（Admin），覆盖患者自助服务全流程与医院运营管理功能。

---

## 功能特性

### 自助机端 (Kiosk-Frontend)

| 模块 | 功能 |
|------|------|
| **身份认证** | 身份证 / 医保卡 / 手机号三种验证方式 |
| **当日挂号** | 选科室 → 选医生 → 选号源 → 确认 → 支付（模拟微信/支付宝/银行卡/医保） |
| **预约挂号** | 同上，支持提前预约 |
| **门诊缴费** | 查看待缴费项目并在线支付 |
| **住院充值** | 查询住院信息并缴纳预交金 |
| **挂号记录** | 查询历史挂号记录及状态 |
| **缴费查询** | 查询门诊缴费明细 |
| **住院查询** | 查询住院费用及充值记录 |
| **药品价格** | 查询医院药品价格 |
| **排队叫号** | 查看当前排队进度 |
| **排队签到** | 到院后自助签到进入排队队列 |

### 管理后台 (Admin-Frontend)

| 模块 | 功能 |
|------|------|
| **首页概览** | 今日交易笔数/金额、退款笔数、在线设备数 |
| **交易流水** | 按业务类型、支付状态、日期查询交易记录与支付明细 |
| **对账管理** | 按渠道（微信/支付宝/银行卡/医保/HIS）执行对账、查看差异明细、生成差异工单 |
| **退款管理** | 退款申请审批（通过/驳回） |
| **日终结算** | 生成结算单、确认、锁定 |
| **设备管理** | 查看自助机状态、心跳、修改功能配置 |
| **规则配置** | 动态配置业务规则 |
| **黑名单管理** | 添加/移除黑名单患者 |
| **异常监控** | 检测频繁挂号退号等异常行为 |

### 支付方式

- **微信支付** — 模拟扫码支付
- **支付宝** — 模拟扫码支付
- **银行卡** — 模拟刷卡支付
- **医保支付** — 模拟医保结算

---

## 技术栈

### 后端 (hospital-backend)

| 技术 | 版本 |
|------|------|
| Java | 17+ |
| Spring Boot | 3.2.5 |
| MyBatis-Plus | 3.5.6 |
| MySQL | 8.0 |
| Redis | 7.x |
| Maven | 多模块架构 |

**9 个子模块：**

| 模块 | 职责 |
|------|------|
| `hospital-common` | 统一响应体(R)、全局异常处理、Jackson 配置、工具类 |
| `hospital-model` | 实体类(23个)、DTO、枚举 |
| `hospital-dal` | MyBatis-Plus Mapper + 分页配置 + 自动填充 |
| `hospital-his-adapter` | HIS 系统接口抽象 + HisMockClient 模拟实现 |
| `hospital-payment` | 支付策略模式(微信/支付宝/银行卡/医保) + 回调分发 |
| `hospital-hardware` | 硬件抽象层(读卡器/打印机/扫码枪) + Mock实现 |
| `hospital-service` | 业务逻辑：挂号/门诊/住院/支付/退款/对账/结算/排队/签到/规则/黑名单/异常检测 |
| `hospital-job` | 定时任务：每日对账(2AM)、超时关单(1min)、设备离线检测(1min)、异常检测(5min) |
| `hospital-web` | Controller(自助机/管理后台/回调)、CORS 配置、启动入口 |

### 自助机前端 (kiosk-frontend)

| 技术 | 用途 |
|------|------|
| Vue 3 (Composition API) | 框架 |
| Vite 6 | 构建工具 |
| TypeScript | 类型安全 |
| Element Plus | UI 组件库 |
| Pinia | 状态管理 |
| Vue Router | 路由 |
| Axios | HTTP 请求 |

### 管理后台前端 (admin-frontend)

同自助机前端技术栈，布局为侧边栏 + 顶栏管理界面。

---

## 数据库 (23张表)

| 表名 | 说明 |
|------|------|
| `patient` | 患者信息 |
| `department` | 科室 |
| `doctor` | 医生 |
| `schedule` | 排班(号源) |
| `schedule_lock` | 号源锁 |
| `registration` | 挂号记录 |
| `queue` | 排队记录 |
| `outpatient_order` | 门诊缴费单 |
| `outpatient_order_item` | 门诊缴费明细 |
| `inpatient_deposit` | 住院充值记录 |
| `drug_info` | 药品信息 |
| `payment_order` | 支付订单 |
| `payment_detail` | 支付明细 |
| `refund_order` | 退款订单 |
| `reconciliation_record` | 对账记录 |
| `reconciliation_detail` | 对账明细 |
| `reconciliation_ticket` | 差异工单 |
| `daily_settlement` | 日终结算 |
| `kiosk_machine` | 自助机设备 |
| `kiosk_heartbeat` | 设备心跳 |
| `biz_rule` | 业务规则 |
| `sys_user` | 管理后台用户 |
| `patient_blacklist` | 患者黑名单 |

> 完整建表脚本：`hospital-backend/sql/schema.sql`
>
> 测试数据：`hospital-backend/sql/test-data.sql`（14个科室、20名医生、32个号源、5名患者、12种药品、5台自助机）

---

## 快速启动

### 前置条件

- JDK 17+
- Maven 3.8+
- MySQL 8.0
- Redis 7.x
- Node.js 18+
- npm 或 pnpm

### 本地开发

```bash
# 1. 初始化数据库
mysql -u root -p < hospital-backend/sql/schema.sql
mysql -u root -p < hospital-backend/sql/test-data.sql

# 2. 启动后端（端口 8080）
cd hospital-backend
mvn spring-boot:run -pl hospital-web

# 3. 启动自助机前端（端口 3000）
cd kiosk-frontend
npm install
npm run dev

# 4. 启动管理后台（端口 3001）
cd admin-frontend
npm install
npm run dev
```

访问：

- 自助机：`http://localhost:3000`
- 管理后台：`http://localhost:3001`
- 后端 API：`http://localhost:8080`

### 管理后台默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | SUPER_ADMIN |

### Docker 一键部署

```bash
cd deploy
docker-compose up -d
```

---

## API 概览

### 自助机接口 (`/kiosk/`)

| 路径 | 方法 | 说明 |
|------|------|------|
| `/kiosk/patient/verify` | POST | 身份验证 |
| `/kiosk/registration/departments` | GET | 科室列表 |
| `/kiosk/registration/doctors` | GET | 医生列表 |
| `/kiosk/registration/schedules` | GET | 号源列表 |
| `/kiosk/registration/register` | POST | 确认挂号 |
| `/kiosk/payment/pay` | POST | 支付 |
| `/kiosk/queue/detail` | GET | 排队信息 |
| `/kiosk/signin/sign` | POST | 排队签到 |

### 管理后台接口 (`/admin/`)

| 路径 | 方法 | 说明 |
|------|------|------|
| `/admin/auth/login` | POST | 登录 |
| `/admin/dashboard/summary` | GET | 首页统计 |
| `/admin/transaction/list` | GET | 交易流水 |
| `/admin/recon/execute` | POST | 执行对账 |
| `/admin/refund/list` | GET | 退款列表 |
| `/admin/settlement/generate` | POST | 生成结算 |
| `/admin/kiosk/list` | GET | 设备列表 |
| `/admin/rule/list` | GET | 规则列表 |
| `/admin/blacklist/list` | GET | 黑名单 |
| `/admin/anomaly/detect` | GET | 异常检测 |

---

## 项目结构

```
smartHospital/
├── hospital-backend/               # Spring Boot 后端
│   ├── hospital-common/            # 统一响应、异常、工具类
│   ├── hospital-model/             # 实体、DTO、枚举
│   ├── hospital-dal/               # MyBatis-Plus Mapper
│   ├── hospital-his-adapter/       # HIS 适配层（接口 + Mock）
│   ├── hospital-payment/           # 支付策略模块
│   ├── hospital-hardware/          # 硬件抽象层（模拟）
│   ├── hospital-service/           # 业务逻辑层
│   ├── hospital-job/               # 定时任务
│   ├── hospital-web/               # Controller + 启动类
│   └── sql/                        # 建表 + 测试数据
│
├── kiosk-frontend/                 # 自助机前端 (Vue3)
├── admin-frontend/                 # 管理后台前端 (Vue3)
└── deploy/                         # Docker 部署配置
```

---

## 架构设计要点

### 支付策略模式
```java
public interface PaymentStrategy {
    String getPayMethod();
    PayResult createPayment(outTradeNo, amount, description);
    PayResult queryPayment(outTradeNo);
    RefundResult refund(outTradeNo, refundNo, amount);
}
```
四种支付方式（微信/支付宝/银行卡/医保）各自实现 `PaymentStrategy`，通过策略模式注入 `Map<String, PaymentStrategy>`，便于扩展。

### HIS 系统解耦
```java
public interface HisClient {
    OutpatientOrderDTO getOutpatientOrder(visitNo);
    List<OutpatientItemDTO> getOutpatientItems(orderNo);
    InpatientInfoDTO getInpatientInfo(inpatientNo);
    // ...
}
```
通过接口定义 HIS 交互契约，`HisMockClient` 提供模拟实现，可无缝切换真实 HIS 对接。

### 硬件抽象
```java
public interface CardReaderDriver { CardReadResult readCard(); }
public interface PrinterDriver { void print(String content); }
public interface ScannerDriver { String scan(); }
```
读卡器、打印机、扫码枪均抽象为接口 + Mock 实现，无需真实硬件即可开发测试。

### Redis 分布式锁
挂号时使用 Redis 锁防止号源超卖（5 分钟自动过期），配合 `schedule_lock` 表做持久化记录。

---

## License

MIT
