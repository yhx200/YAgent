package com.yagent.platform.dto.tool;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ToolVersionResponse {

    private String toolId;

    private String version;

    /**
     * 原始 yagent-tool.json
     */
    private JsonNode manifest;

    private Artifact artifact;

    @Data
    public static class Artifact {

        private String sha256;

        private String signature;
    }
}
