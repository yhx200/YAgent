YAgent Full Starter

这是按当前 YAgent 方案落地的一套 可运行 MVP 工程，目标是先跑通：

用户
  -> YAgent Runtime
  -> capability.search
  -> YAgent Platform
  -> capability.acquire
  -> Tool Materializer
  -> Session-scoped Tool Registry
  -> Tool Proxy
  -> Node Runner
  -> demo-weather Tool
  -> Audit
  -> 最终回答

1. 最重要的安全边界

Runtime 主进程 不会 import 下载下来的第三方 Tool 代码。Runtime 只注册 Tool Schema + Proxy。真正 Tool 代码由 runtimes/node-runner 执行。

开发版 Node Runner 使用“独立 Runner 容器 + 每次调用独立子进程”。生产环境应继续升级成一次性 Tool Container / Job，并加网络 Egress Proxy、Seccomp、资源限制等。

2. 工程结构

yagent/
├── apps/
│   ├── yagent-platform/       # Java 21 + Spring Boot + MyBatis + MySQL
│   ├── yagent-runtime/        # Node 22 + TypeScript Agent Runtime
│   ├── yagent-gateway/        # Gateway 骨架（M1 暂不参与运行）
│   └── yagent-web/            # UI 占位（M1 不做）
├── packages/
│   ├── yagent-tool-protocol/
│   ├── yagent-runtime-core/
│   ├── yagent-runtime-adapter-deepseek/
│   ├── yagent-tool-sdk-node/
│   └── yagent-common/
├── runtimes/
│   └── node-runner/
├── tools/
│   └── demo-weather/
├── infra/
│   ├── docker-compose.yml
│   └── sql/001_schema_and_seed.sql
├── runtime-data/
│   └── tool-store/com.yagent.weather/1.0.0/package.ytool
├── docs/
└── scripts/

3. 环境

建议：

Node.js 22+

pnpm 10+

Java 21

Docker Desktop / Docker Engine

Windows CMD 安装 pnpm：

npm install -g pnpm

如果安装成功但命令找不到，执行：

npm config get prefix

把输出目录加入 Windows 用户 Path。

4. 第一次启动（最简单：Docker）

4.1 复制环境变量

Windows：

copy .env.example .env

Linux / WSL：

cp .env.example .env

默认 YAGENT_LLM_MODE=mock，因此 不需要 DeepSeek Key 也能跑通完整动态 Tool 链路。

4.2 启动

docker compose -f infra/docker-compose.yml --env-file .env up --build

第一次会构建 Java Platform、Runtime、Node Runner。

4.3 健康检查

Platform: http://localhost:8080/actuator/health
Runtime : http://localhost:3000/runtime/health
Runner  : http://localhost:8090/health

5. 第一个完整测试

POST：

http://localhost:3000/runtime/chat

Body：

{
  "tenantId": 10001,
  "userId": 20001,
  "sessionId": "S001",
  "message": "帮我查询上海天气"
}

Mock Adapter 会自动演示：

1. capability_search
2. 找到 weather.current
3. capability_acquire
4. Runtime 下载并校验 com.yagent.weather@1.0.0
5. 注册 weather_query Proxy（注意：没有 import Tool）
6. weather_query -> node-runner
7. node-runner 子进程加载 Tool
8. 返回 上海 26°C 晴
9. 写 ya_audit_event

预期响应类似：

{
  "success": true,
  "data": {
    "sessionId": "S001",
    "reply": "查询完成：上海当前 26°C，晴。"
  }
}

6. 切到真实 DeepSeek

编辑 .env：

YAGENT_LLM_MODE=deepseek
DEEPSEEK_API_KEY=你的Key
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_MODEL=deepseek-chat

然后重启 Runtime。

设计上 Runtime 只依赖 AgentRuntimeAdapter，以后接 DeepSeek Harness 时，只替换 packages/yagent-runtime-adapter-deepseek 这一层即可，不需要重写 Capability / Tool / Permission / Sandbox。

7. Platform 数据库 8 张核心表

本项目已经包含：

ya_capability

ya_tool

ya_tool_version

ya_tool_capability

ya_tool_permission

ya_tenant_installation

ya_tenant_tool_policy

ya_audit_event

并带 weather.current -> com.yagent.weather@1.0.0 -> weather.query 测试数据。

8. Platform 内部 API

主要接口：

POST /inner/v1/capabilities/search
POST /inner/v1/installations/resolve
GET  /inner/v1/tools/{toolId}/versions/{version}
POST /inner/v1/permissions/evaluate
POST /inner/v1/tool-packages/download-ticket
GET  /inner/v1/tool-packages/file
POST /inner/v1/audit-events

Runtime 不直接访问 MySQL。

9. 本地非 Docker 开发

Platform

需要本机 Maven 3.9+、Java 21：

cd apps/yagent-platform
mvn spring-boot:run

Node workspace

pnpm install
pnpm build

Runner：

pnpm dev:runner

Runtime：

pnpm dev:runtime

本地跑 Runtime 时把 .env 中地址改为：

YAGENT_PLATFORM_BASE_URL=http://localhost:8080
YAGENT_RUNNER_BASE_URL=http://localhost:8090
YAGENT_TOOL_CACHE_ROOT=./runtime-data/tool-cache
YAGENT_RUNNER_TOOL_ROOT=./runtime-data/tool-cache

10. 重新打 demo-weather 包

修改 tools/demo-weather 后：

pnpm install
pnpm --filter @yagent/demo-weather build
pnpm package:weather

脚本会生成：

runtime-data/tool-store/com.yagent.weather/1.0.0/package.ytool

注意：重新打包后 SHA256 会变化，需要同步更新数据库 ya_tool_version.package_sha256。

11. 生产环境还必须继续补的安全能力

本 Starter 是架构闭环，不等于完整生产安全边界。生产必须继续加入：

一次一容器/Job 的 Tool 执行

容器 CPU / Memory / PIDs / timeout 限制

rootless / seccomp / AppArmor

Tool package 签名验签

Publisher 信任链

上传时依赖扫描、恶意代码扫描

Egress Proxy 域名白名单

用户审批 REQUIRE_APPROVAL

AuditRedactor（敏感字段脱敏）

Gateway -> Runtime 服务身份认证

Redis Session / Runtime sticky routing

Tool Package 从本地文件切换到 MinIO presigned URL

12. 建议开发顺序

M1 动态能力闭环（本项目已实现）
  ↓
M2 强 Sandbox
  ↓
M3 Permission / Approval 完善
  ↓
M4 Gateway / Tenant / Trace
  ↓
M5 MCP Provider
  ↓
M6 Marketplace / Publisher Center
