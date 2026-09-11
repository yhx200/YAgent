package com.yagent.platform.domain.capability;

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

    /**
     * weather.query
     */
    private String toolName;

    private Integer priority;
}
