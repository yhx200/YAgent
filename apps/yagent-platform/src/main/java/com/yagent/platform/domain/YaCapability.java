package com.yagent.platform.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class YaCapability {
    private Long id;
    private String capabilityCode;
    private String name;
    private String description;
    private String category;
    private String keywords;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
