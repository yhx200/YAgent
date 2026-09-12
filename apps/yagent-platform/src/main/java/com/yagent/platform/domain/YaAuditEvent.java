package com.yagent.platform.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class YaAuditEvent {
    private Long id;
    private String traceId;
    private Long tenantId;
    private Long userId;
    private String sessionId;
    private String eventType;
    private String capabilityCode;
    private String toolId;
    private String toolVersion;
    private String toolName;
    private String resultStatus;
    private Long durationMs;
    private String detailJson;
    private LocalDateTime createTime;
}
