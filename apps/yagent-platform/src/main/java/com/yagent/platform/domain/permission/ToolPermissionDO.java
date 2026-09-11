package com.yagent.platform.domain.permission;

import lombok.Data;

@Data
public class ToolPermissionDO {

    private Long id;

    private String toolId;

    private String version;

    private String permissionType;

    private String permissionValue;
}
