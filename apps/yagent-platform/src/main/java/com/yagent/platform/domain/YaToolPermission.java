package com.yagent.platform.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class YaToolPermission {
    private Long id;
    private String toolId;
    private String version;
    private String permissionType;
    private String permissionValue;
}
