package com.yagent.platform.dto.packageinfo;

import lombok.Data;

@Data
public class ToolPackageTicketResponse {

    /**
     * MinIO 临时下载地址
     */
    private String downloadUrl;

    /**
     * 单位秒
     */
    private Integer expiresIn;

    private String sha256;

    private String signature;
}
