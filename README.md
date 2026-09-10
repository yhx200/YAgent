# YAgent
你的这个 Yagent，本质上不是普通聊天 Agent，也不只是多 Agent 协作，而是一个：

具备“发现工具 → 下载工具 → 安装/装载工具 → 调用工具 → 必要时卸载工具”能力的动态 Agent Runtime。

所以如果按你描述的目标来选框架，我会优先考虑 DeepSeek Harness，而不是 AutoGen。原因是它的架构和你的需求非常贴合：它本身就是“Everything is a Plugin”，工具注册、Agent Loop、Session、模型适配器等都是插件，而且已经有运行时动态扩展、检查已加载插件、定义/运行动态包、撤销动态包的机制。

你这个 Yagent 可以设计成下面这样：

用户：
“帮我把这个 Excel 转成 PDF”

        ↓

Yagent 理解用户意图
        ↓
当前工具集合中有没有？
        │
   ┌────┴────┐
   │         │
   有        没有
   │         │
直接调用     ↓
         Tool Dictionary
             ↓
      搜索 "excel to pdf"
             ↓
      返回候选工具
             ↓
       Tool Installer
             ↓
       下载工具包
             ↓
     校验 / Sandbox
             ↓
       Tool Loader
             ↓
   注册进 Tool Registry
             ↓
       Agent 再次推理
             ↓
         调用工具
             ↓
          返回结果

这里最关键的一点是：

“字典也是工具”这个设计是对的。

初始 Agent 不需要拥有 500 个工具，只需要少量 bootstrap 工具，例如：

dictionary.search
tool.describe
tool.install
tool.load
tool.unload

例如初始工具只有：

{
  "name": "dictionary.search",
  "description": "根据用户需求搜索可用工具"
}

用户说：

帮我查一下上海明天的天气

LLM发现当前没有天气工具，于是：

dictionary.search(
    query = "查询城市天气预报"
)

字典返回：

[
  {
    "toolId": "weather-cn",
    "name": "中国天气查询",
    "version": "1.3.2",
    "description": "查询中国城市实时天气和天气预报",
    "download": "...",
    "permissions": [
      "network"
    ]
  }
]

然后：

tool.install("weather-cn")

再：

tool.load("weather-cn")

Tool Registry 从：

dictionary.search
tool.install
tool.load

动态变成：

dictionary.search
tool.install
tool.load
weather.current
weather.forecast

然后模型下一轮就可以：

weather.forecast(city="上海")

这跟 DeepSeek Harness 的工具模型很契合：它本身就有动态 Tool Registry，工具插件负责注册 schema 和 executor；执行还会经过 pre-execute、execute、post-execute 等管道。

为什么我不首先推荐 AutoGen

AutoGen 更适合：

Planner Agent
    ↓
Developer Agent
    ↓
Tester Agent
    ↓
Reviewer Agent

即：

多个 Agent 如何通信和分工。

而你的核心问题不是“几个 Agent 怎么协作”，而是：

Agent 当前不会某件事
        ↓
自己寻找能力
        ↓
获取能力
        ↓
装载能力
        ↓
获得新 Tool Schema
        ↓
继续工作

这实际上是一个 动态能力系统 / Plugin Runtime。

所以你的核心抽象应该是：

Model
Agent Loop
Tool Registry
Plugin Manager
Tool Dictionary
Installer
Sandbox
Permission
Session

这正是 Harness 类框架的方向。DeepSeek Harness 的 Tool Registry 本身就是动态注册式设计，而模型看到的工具 schema 由 Registry 生成。

甚至它现在已经有一套很接近你想法的 extensions 子系统：

Agent 可以检查自己运行环境里的插件和服务，也可以定义、运行和撤销动态包。

这其实已经开始接近“Agent 修改自己的运行能力”了。

不过，我建议你不要直接把 Yagent = DeepSeek Harness 二次开发。

更好的架构是：

                Yagent
                  │
        ┌─────────┴─────────┐
        │                   │
     Agent Core        Tool Platform
        │                   │
        │              Tool Registry
        │                   │
        │              Tool Store
        │                   │
        │              Tool Package
        │                   │
        └──── Tool API ─────┘

Yagent 自己定义一套稳定的工具协议。

例如：

{
  "id": "com.yagent.weather",
  "name": "weather",
  "version": "1.2.0",

  "tools": [
    {
      "name": "weather.query",
      "description": "查询天气",
      "inputSchema": {
        "type": "object",
        "properties": {
          "city": {
            "type": "string"
          }
        }
      }
    }
  ],

  "runtime": {
    "type": "node",
    "entry": "index.js"
  },

  "permissions": [
    "network"
  ]
}

以后你的 Tool Store 中所有工具都遵循这个协议。

这样底层 Agent Runtime 可以换：

DeepSeek Harness
         ↓
Yagent Tool Protocol
         ↓
Yagent Tool Store

以后哪怕不用 DeepSeek Harness，也可以变成：

LangGraph
         ↓
Yagent Tool Protocol
         ↓
Yagent Tool Store

甚至：

自己实现 Agent Loop
         ↓
Yagent Tool Protocol
         ↓
Yagent Tool Store

这样不会被一个框架锁死。

我认为 Yagent 最重要的其实不是 Agent，而是 Tool Platform。

你可以把整个项目拆成 6 个核心模块：

Yagent
│
├── 1. Agent Runtime
│      ├── Model
│      ├── Agent Loop
│      ├── Context
│      └── Session
│
├── 2. Tool Registry
│      ├── register()
│      ├── unregister()
│      ├── list()
│      └── invoke()
│
├── 3. Tool Dictionary
│      ├── semanticSearch()
│      ├── keywordSearch()
│      ├── categorySearch()
│      └── recommend()
│
├── 4. Tool Store
│      ├── package
│      ├── version
│      ├── metadata
│      ├── dependency
│      └── permissions
│
├── 5. Tool Manager
│      ├── download()
│      ├── verify()
│      ├── install()
│      ├── load()
│      ├── unload()
│      └── upgrade()
│
└── 6. Sandbox
       ├── filesystem
       ├── network
       ├── command
       ├── memory
       └── timeout

这里面真正构成你护城河的是：

Tool Dictionary
+
Tool Store
+
Tool Protocol
+
Tool Manager

而不是 LLM。

还有一个设计我强烈建议你采用：

不要把全部工具 description 塞进模型上下文。

比如以后你平台有：

10000 个工具

不能：

LLM
 ↓
10000 个 tool schemas

上下文会直接爆炸。

应该是：

LLM 初始只有 5~10 个系统工具

dictionary.search
dictionary.describe
tool.install
tool.load
tool.remove

然后采用：

用户语言
 ↓
LLM
 ↓
dictionary.search
 ↓
向量搜索 + 关键词搜索
 ↓
Top 5 tools
 ↓
tool.describe
 ↓
选择 Tool
 ↓
安装
 ↓
Registry
 ↓
模型获得新 Tool Schema

也就是说：

Tool Dictionary 本身就是 Tool RAG。

例如：

用户：
“帮我分析一下这个 jar 为什么启动失败。”

Dictionary 搜：

Java
JAR
Spring Boot
log
Maven
diagnostic

最终得到：

java-log-analyzer
maven-runner
jar-inspector

装载后：

Yagent
 ├── dictionary.search
 ├── tool.install
 ├── tool.load
 ├── java.inspectJar
 ├── java.analyzeLog
 └── maven.run

Agent 能力就动态增长了。

还有一个非常关键的问题：

一定不能让 Agent 随便下载东西直接执行

否则你的架构很快会变成：

用户
 ↓
Prompt Injection
 ↓
Agent
 ↓
下载恶意工具
 ↓
执行
 ↓
服务器没了

所以必须：

Tool Store
   ↓
签名
   ↓
SHA256
   ↓
Publisher
   ↓
Permission Manifest
   ↓
Sandbox
   ↓
Tool Runtime

例如 Tool 声明：

{
  "permissions": {
    "network": [
      "api.weather.com"
    ],
    "filesystem": {
      "read": [
        "/workspace"
      ],
      "write": [
        "/workspace/output"
      ]
    },
    "shell": false
  }
}

然后 Yagent 决定：

允许安装 ≠ 允许所有权限

允许安装
  ↓
检查权限
  ↓
用户 / 策略审批
  ↓
Sandbox 执行

DeepSeek Harness 本身也把 Sandbox、Approval Policy、Tools execution pipeline 放在基础架构里，这也是为什么它比单纯的 AutoGen 更适合这个项目。

如果按照你现在的技术背景来做

你主要是：

Java
Spring Boot
MySQL
Spring Cloud Alibaba
Nacos

但 Yagent Core 我反而不建议全用 Java。

可以考虑：

                  Yagent
                    │
      ┌─────────────┴─────────────┐
      │                           │
 Agent Runtime                Platform
 TypeScript                   Java
 DeepSeek Harness             Spring Boot
      │                           │
      │                       MySQL
      │                       Redis
      │                       Nacos
      │
      └──────── HTTP/gRPC ────────┘

即：

Agent Runtime：TypeScript

DeepSeek Harness
Tool Loader
Sandbox
Agent Loop

平台后端：Java

账号
租户
Tool Store
权限
计费
审计
版本
发布

你现有的 Spring Boot 技术栈完全可以继续使用。

所以，如果现在让我帮你拍板：

方案	推荐程度
DeepSeek Harness	⭐⭐⭐⭐⭐
LangGraph	⭐⭐⭐⭐
AutoGen	⭐⭐⭐
CrewAI	⭐⭐
从零实现	前期 ⭐⭐，后期可考虑

我的建议是：

Yagent V1：DeepSeek Harness + 自己设计 Yagent Tool Protocol + Tool Dictionary + Tool Store。

而不是：

AutoGen + 一堆动态 Tool。

因为 Yagent 的灵魂并不是 Multi-Agent，而是：

Self-extensible Agent —— 智能体能够主动发现并装载新的能力。

而 DeepSeek Harness 现在甚至已经支持 Code Runtime，并把运行模型生成代码的执行环境抽象为可替换 provider，这对以后实现 Python/Node/WASM 等不同 Tool Runtime 也非常有用。

不过有一点必须考虑：截至 2026 年 9 月，DeepSeek Harness 官方仍然明确标记为 Developer Preview，API 可能发生 breaking changes。

所以我会把它当成：

Yagent
   ↓
Yagent Runtime Adapter
   ↓
DeepSeek Harness

中间一定隔一层自己的 Adapter。

这样以后换 Harness 不会把整个 Yagent 推倒重写。

最终架构我会定成：

           Yagent
              │
        Agent Runtime
              │
     ┌────────┴─────────┐
     │                  │
Dictionary Tool     Tool Manager
     │                  │
Tool Search          install
     │               load
Vector DB            unload
     │               upgrade
     └────────┬─────────┘
              │
        Tool Registry
              │
        Tool Sandbox
              │
         Tool Runtime
        /     |      \
    Node    Python    WASM

这个架构后面甚至可以发展成一个 “Agent App Store”：开发者上传 Tool，Yagent 根据自然语言自动发现、安装和使用能力。你这个方向从产品形态上是成立的，而且 DeepSeek Harness 的插件和动态运行时思想正好能给你省掉大量底层工作。
