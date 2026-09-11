package com.yagent.platform.domain;

import lombok.Data;

import java.util.Date;

@Data
public class CapabilityDO {

    private Long id;

    private String capabilityCode;

    private String capabilityName;

    private String description;

    private String keywords;

    private String providerType;

    private String toolId;

    private String toolVersion;

    private String status;

    private Date createTime;

    private Date updateTime;
}
