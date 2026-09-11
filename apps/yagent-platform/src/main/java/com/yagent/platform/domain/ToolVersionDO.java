package com.yagent.platform.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ToolVersionDO {

    private Long id;

    private String toolId;

    private String version;

    private String manifestJson;

    private String bucket;

    private String objectKey;

    private String sha256;

    private String signature;

    private String status;

    private Date createTime;

    private Date updateTime;
}
