package com.yagent.platform.domain;

import lombok.Data;

@Data
public class CapabilityProviderDO {

    private String capabilityCode;

    private String capabilityName;

    private String description;

    private String category;

    private String keywords;

    private String toolId;

    private String toolVersion;

    private String toolName;

    private Integer priority;
}
