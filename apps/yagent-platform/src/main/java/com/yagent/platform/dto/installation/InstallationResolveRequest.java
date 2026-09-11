package com.yagent.platform.dto.installation;

import lombok.Data;

@Data
public class InstallationResolveRequest {

    private Long tenantId;

    private String toolId;
}
