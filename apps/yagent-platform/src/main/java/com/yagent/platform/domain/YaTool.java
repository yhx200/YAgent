package com.yagent.platform.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class YaTool {
    private Long id;
    private String toolId;
    private String name;
    private String displayName;
    private String description;
    private Long publisherId;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
