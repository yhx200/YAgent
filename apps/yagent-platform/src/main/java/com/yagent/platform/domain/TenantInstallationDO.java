package com.yagent.platform.domain;

import lombok.Data;

import java.util.Date;

@Data
public class TenantInstallationDO {

    private Long id;

    private Long tenantId;

    private String toolId;

    private String version;

    private String status;

    private Long installUserId;

    private Date installTime;
}
