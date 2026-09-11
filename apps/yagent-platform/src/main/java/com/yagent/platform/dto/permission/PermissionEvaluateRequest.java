package com.yagent.platform.dto.permission;

import lombok.Data;

@Data
public class PermissionEvaluateRequest {

    private Long tenantId;

    private Long userId;

    private String agentId;

    private String toolId;

    private String version;

    /**
     * 例如：
     * weather.query
     */
    private String toolName;
}
