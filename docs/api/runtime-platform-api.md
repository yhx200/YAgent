# Runtime <-> Platform Internal API

## POST /inner/v1/capabilities/search

```json
{"tenantId":10001,"query":"查询上海天气","limit":5}
```

## POST /inner/v1/installations/resolve

```json
{"tenantId":10001,"capabilityCode":"weather.current"}
```

## POST /inner/v1/permissions/evaluate

```json
{"tenantId":10001,"userId":20001,"sessionId":"S001","toolId":"com.yagent.weather","version":"1.0.0","toolName":"weather.query","stage":"EXECUTE"}
```

## POST /inner/v1/tool-packages/download-ticket

```json
{"toolId":"com.yagent.weather","version":"1.0.0"}
```

## POST /inner/v1/audit-events

由 Runtime 记录实际 Tool 调用结果。
