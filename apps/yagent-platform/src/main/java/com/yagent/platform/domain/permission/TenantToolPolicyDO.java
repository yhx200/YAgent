package com.yagent.platform.domain.permission;

import lombok.Data;

@Data
public class TenantToolPolicyDO {

    private Long id;

    private Long tenantId;

    private String toolId;

    private String policyType;

    private String policyValue;

    private String status;
}
