package com.yagent.platform.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class YaTenantInstallation {
    private Long id;
    private Long tenantId;
    private String toolId;
    private String version;
    private String status;
    private Long installUserId;
    private LocalDateTime installTime;
}
