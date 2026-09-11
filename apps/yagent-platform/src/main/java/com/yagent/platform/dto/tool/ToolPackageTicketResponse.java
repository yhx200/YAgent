package com.yagent.platform.dto.tool;

import lombok.Data;

@Data
public class ToolPackageTicketResponse {

    private String downloadUrl;

    private Integer expiresIn;

    private String sha256;

    private String signature;
}
