package com.yagent.platform.service.tool.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yagent.platform.domain.ToolVersionDO;
import com.yagent.platform.dto.tool.ToolVersionResponse;
import com.yagent.platform.exception.BizException;
import com.yagent.platform.mapper.tool.ToolVersionMapper;
import com.yagent.platform.service.tool.ToolVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ToolVersionServiceImpl implements ToolVersionService {
    @Autowired
    private ToolVersionMapper toolVersionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ToolVersionResponse getVersion(
            String toolId,
            String version) {

        if (!StringUtils.hasText(toolId)) {

            throw new BizException(
                    "INVALID_ARGUMENT",
                    "toolId cannot be empty"
            );
        }

        if (!StringUtils.hasText(version)) {

            throw new BizException(
                    "INVALID_ARGUMENT",
                    "version cannot be empty"
            );
        }

        ToolVersionDO toolVersion =
                toolVersionMapper
                        .selectByToolIdAndVersion(
                                toolId,
                                version
                        );

        if (toolVersion == null) {

            throw new BizException(
                    "TOOL_VERSION_NOT_FOUND",
                    "Tool version not found: "
                            + toolId
                            + ":"
                            + version
            );
        }

        if (!"PUBLISHED".equals(
                toolVersion.getStatus())) {

            throw new BizException(
                    "TOOL_VERSION_UNAVAILABLE",
                    "Tool version unavailable"
            );
        }

        JsonNode manifest;

        try {

            manifest =
                    objectMapper.readTree(
                            toolVersion.getManifestJson()
                    );

        } catch (Exception e) {

            throw new BizException(
                    "INVALID_TOOL_MANIFEST",
                    "Invalid tool manifest",
                    e
            );
        }

        ToolVersionResponse response =
                new ToolVersionResponse();

        response.setToolId(
                toolVersion.getToolId()
        );

        response.setVersion(
                toolVersion.getVersion()
        );

        response.setManifest(manifest);

        ToolVersionResponse.Artifact artifact =
                new ToolVersionResponse.Artifact();

        artifact.setSha256(
                toolVersion.getSha256()
        );

        artifact.setSignature(
                toolVersion.getSignature()
        );

        response.setArtifact(artifact);

        return response;
    }
}
