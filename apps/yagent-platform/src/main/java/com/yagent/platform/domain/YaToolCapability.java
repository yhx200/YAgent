package com.yagent.platform.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class YaToolCapability {
    private Long id;
    private String toolId;
    private String toolVersion;
    private String capabilityCode;
    private String toolName;
    private Integer priority;
}
