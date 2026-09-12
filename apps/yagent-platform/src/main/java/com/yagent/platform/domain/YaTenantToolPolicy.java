package com.yagent.platform.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class YaTenantToolPolicy {
    private Long id;
    private Long tenantId;
    private String toolId;
    private String policyType;
    private String policyValue;
    private String status;
}
