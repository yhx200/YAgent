# YAgent Architecture

## 主干

```text
User Intent
  -> Agent
  -> Capability Missing?
  -> Capability Resolver
  -> Capability Dictionary
  -> Provider Selection
  -> Policy / Permission
  -> Installation
  -> Materialization
  -> Tool Schema (session scoped)
  -> Next Agent Step
  -> Tool Proxy
  -> Permission (execute gate)
  -> Sandbox Runtime
  -> Actual Tool
  -> Audit
  -> Result
```

## 安全原则

1. Runtime/Harness 主进程永远不 import 第三方 Tool 包。
2. Session Tool Registry 隔离，不做 Runtime 全局可见 Registry。
3. Activate 与 Execute 各检查一次 Permission。
4. Package 下载后校验 SHA256；生产再加签名验签。
5. Tool 实际代码只能在 Runner/Sandbox 执行。
6. Audit 默认只记参数键名，不原样保存敏感参数。
