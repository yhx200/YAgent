package com.yagent.platform.dto.tool;

import lombok.Data;

@Data
public class ToolPackageTicketRequest {

    private Long tenantId;

    private String toolId;

    private String version;
}
