package com.yagent.platform.domain.tool;

import lombok.Data;

import java.util.Date;

@Data
public class ToolVersionDO {

    private Long id;

    private String toolId;

    private String version;

    private String protocolVersion;

    private String runtimeType;

    private String manifestJson;

    private String packageObjectKey;

    private String packageSha256;

    private String signature;

    private String status;

    private Date createTime;
}
