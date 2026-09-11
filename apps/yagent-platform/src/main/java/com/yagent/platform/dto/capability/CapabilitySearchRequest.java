package com.yagent.platform.dto.capability;

import lombok.Data;

@Data
public class CapabilitySearchRequest {

    private Long tenantId;

    private String query;

    private Integer limit;
}
