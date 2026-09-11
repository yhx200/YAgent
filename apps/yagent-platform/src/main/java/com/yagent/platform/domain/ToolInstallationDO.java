package com.yagent.platform.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ToolInstallationDO {

    private Long id;

    private Long tenantId;

    private String toolId;

    private String version;

    private String status;

    private Date createTime;

    private Date updateTime;
}
