有，而且现在已经出现了几种非常接近你这个 Yagent 思路的架构，只是大多数还没完全做到你设想的“自然语言 → 字典搜索 → 自动下载安装 → 动态装载 → 立即调用”这一整套闭环。

最接近的我会分成 4 类来看。

第一类就是 DeepSeek Harness。它和你设想的架构最像，因为它从底层就把“能力”做成插件：模型、工具、Skill、Session、Sandbox、Storage、Loop、Scheduling、UI 都可以插件化，而且 Cordis 内核负责插件的挂载、卸载和依赖管理。更关键的是，它已经有 extensions 子系统，可以让 Agent 检查当前运行时、定义动态 package、运行、再撤回，也就是“Agent 修改自己运行时能力”已经是官方设计的一部分。

你想要的：

用户需求
  ↓
发现缺少工具
  ↓
搜索工具
  ↓
下载安装
  ↓
load 到 runtime
  ↓
工具 schema 出现在 Agent 中
  ↓
继续调用

DeepSeek Harness 现在最接近的是：

Agent
  ↓
inspect runtime
  ↓
发现/定义 extension
  ↓
mount plugin
  ↓
tool registry 更新
  ↓
调用新能力

所以它已经有你架构大概 60%~70% 的底座。

第二类是 MCP 生态。

这个和你的“工具字典”思想也非常接近。

传统 MCP 是：

Agent
  ↓
连接 MCP Server
  ↓
server/list tools
  ↓
得到工具
  ↓
调用工具

而现在已经进一步出现了 Dynamic Tool Discovery。例如 Microsoft 365 Copilot 在 2026 年已经支持 MCP 插件的运行时工具发现：Agent 不需要在发布时固定完整工具列表，而可以运行时从 MCP Server 获取当前可用工具。

它其实就是：

Agent
    ↓
MCP Server
    ↓
动态 list tools
    ↓
发现新 Tool
    ↓
调用

但它和你设想的 Yagent 还有一个明显区别：

MCP 解决的是“发现远程工具”，不主要解决“下载安装工具代码”。

你的是：

search
 ↓
download
 ↓
install
 ↓
load
 ↓
invoke

MCP 更多是：

connect
 ↓
discover
 ↓
invoke

所以我认为未来 Yagent 最好的设计不是排斥 MCP，而是：

Yagent Tool Protocol
       │
       ├── Native Plugin
       │
       ├── MCP
       │
       ├── HTTP/OpenAPI
       │
       └── WASM

这样最好。

第三类就是现在 ChatGPT / Codex 的 Plugin Directory 思路。

现在 OpenAI 已经把 Plugins 作为发现工作流能力的主要方式。一个 Plugin 可以包含：

Skills
Apps
App Templates

用户可以在 Plugin Directory 里发现、安装，再连接底层 app。

这和你的产品思路其实高度相似：

             Yagent
                │
         Tool Dictionary
                │
       ┌────────┴────────┐
       │                 │
     Tool              Skill
       │                 │
 Google Drive       "怎么使用Drive"
       │
    OAuth

OpenAI 现在的形态更倾向：

用户安装 Plugin
      ↓
Plugin 暴露能力
      ↓
Agent 使用

而你想再往前一步：

Agent 自己发现需求
      ↓
自己搜索 Plugin
      ↓
自动申请安装
      ↓
根据安全策略决定是否需要用户确认
      ↓
使用

所以你这个其实可以理解成：

Agent-native Plugin Store

而不是传统的人类操作 App Store。

第四类是很多框架开始做的 Tool Registry + Retrieval。

例如 LangGraph、AutoGen 等虽然通常不是“自动下载安装”，但现在很多生产 Agent 都不会把几千个工具一次性交给模型，而是：

用户 Query
    ↓
Tool Retriever
    ↓
向量检索
    ↓
Top-K Tools
    ↓
动态绑定 Tool
    ↓
Agent

你说的“字典也是工具”，其实就是这个方向的进一步升级：

dictionary.search()

它本质上既是：

Tool Registry
+
Tool Retrieval
+
Tool Metadata DB

所以这一块并不是全新的，但你真正可以做出差异的地方在：

Retrieval
    ↓
Acquisition
    ↓
Installation
    ↓
Permission
    ↓
Runtime Loading

这几层连起来。

我会给你一个非常直观的行业位置图：

                    动态发现
                       ↑
                       │
                       │        Yagent
                       │          ★
                       │
           MCP         │
            ★          │
                       │
                       │    DeepSeek Harness
                       │           ★
                       │
 AutoGen ★             │
 LangGraph ★           │
───────────────────────┼────────────────────→
 静态能力              │               动态安装/修改 Runtime
                       │
                       │
     ChatGPT Plugins ★
                       │
                       │

Yagent 真正应该占的位置，是右上角：

动态发现 + 动态获取 + 动态装载。

而且这里我觉得有一个很值得你抓住的点：

现在大部分方案都是：

人 → 找插件 → 安装 → Agent 使用

你想做的是：

Agent → 意识到能力不足
     → 找插件
     → 评估插件
     → 装载插件
     → 用插件

这其实已经从：

Plugin System

变成：

Capability Acquisition System

这是一个更高级的抽象。

所以我甚至不建议你以后把核心模块叫：

Tool Store

可以叫：

Capability Registry

因为以后里面可能不只是 Tool：

Capability
├── Tool
├── Skill
├── MCP Server
├── Agent
├── Workflow
├── Model
└── Runtime

然后：

Capability Dictionary
       ↓
searchCapability()
       ↓
resolve()
       ↓
install()
       ↓
mount()
       ↓
invoke()

这个会比单纯“工具商店”扩展性大很多。

如果现在让我给你一个 Yagent V1 架构建议，我会直接定成这样：

                 Yagent Core
                     │
             Intent / Planner
                     │
          ┌──────────┴──────────┐
          │                     │
 Capability Resolver      Existing Tools
          │
          ↓
 Capability Dictionary
          │
     semanticSearch()
          │
          ↓
   Capability Registry
          │
    ┌─────┴─────────────┐
    │                   │
 Native Plugin          MCP
    │                   │
 download()          connect()
 verify()            discover()
 install()
    │
    └──────────┬────────┘
               ↓
        Permission Engine
               ↓
            Sandbox
               ↓
        Runtime Registry
               ↓
             Agent

底层 Runtime：

DeepSeek Harness

能力协议：

Yagent Capability Protocol

远程工具协议：

MCP

平台层：

Spring Boot
MySQL
Redis
Nacos

你现有 Java 技术栈完全能承担 Registry、权限、账号、版本、商店这些部分。

所以答案是：现在已经有类似架构，但还没有一个主流方案把你想要的整个闭环做到特别完整。

最接近的是：

DeepSeek Harness + MCP + Plugin Store/Registry + Tool Retrieval。

而你真正可以做的，不是再造一个 Agent 框架，而是把这几块统一成：

“Agent 的能力自动获取层”。

这个定位我觉得比“做一个新的 AutoGen”要清晰得多。
