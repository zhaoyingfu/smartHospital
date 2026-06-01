# 智慧医院 



~~~ TXT
smartHospital/
├── hospital-backend/                 # Spring Boot 后端 (9个子模块)
│   ├── hospital-common/              # 统一响应、异常、工具类
│   ├── hospital-model/               # 实体、DTO、VO、枚举
│   ├── hospital-dal/                 # MyBatis-Plus Mapper层
│   ├── hospital-his-adapter/         # HIS适配层 (接口 + Mock实现)
│   ├── hospital-payment/             # 支付模块 (4种策略 + 回调)
│   ├── hospital-hardware/            # 硬件抽象层 (读卡/打印/扫码 模拟)
│   ├── hospital-service/             # 业务逻辑层 (挂号/缴费/对账/退款/...)
│   ├── hospital-job/                 # 定时任务 (对账/关单/心跳/异常检测)
│   ├── hospital-web/                 # Controller + 启动类 + 配置
│   └── sql/schema.sql               # 建表SQL (23张表)
│
├── kiosk-frontend/                   # 自助机前端 (Vue3 + ElementPlus)
│   └── src/
│       ├── views/                    # 首页9宫格 + 身份识别 + 挂号流程
│       ├── stores/                   # Pinia: patient + kiosk
│       ├── composables/              # 超时回首页 + 身份识别
│       └── utils/                    # request + 金额格式化 + 脱敏
│
├── admin-frontend/                   # 管理后台前端 (Vue3 + ElementPlus)
│   └── src/
│       ├── layout/                   # 侧边栏 + 顶栏布局
│       ├── views/                    # 交易/对账/退款/结算/设备/规则/黑名单/异常
│       └── stores/                   # Pinia: user
│
└── deploy/                           # 部署配置
    └── docker-compose.yml           # MySQL + Redis + 后端 + 2个前端
~~~

## 启动方式

1. **数据库**: 执行 `hospital-backend/sql/schema.sql`
2. **后端**: `cd hospital-backend && mvn spring-boot:run -pl hospital-web`
3. **自助机前端**: `cd kiosk-frontend && npm run dev` (端口3000)
4. **管理后台前端**: `cd admin-frontend && npm run dev` (端口3001)
5. **Docker一键启动**: `cd deploy && docker-compose up -d`