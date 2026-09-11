package com.yagent.platform.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ToolDO {

    private Long id;

    private String toolId;

    private String toolName;

    private String description;

    private String status;

    private Date createTime;

    private Date updateTime;
}
