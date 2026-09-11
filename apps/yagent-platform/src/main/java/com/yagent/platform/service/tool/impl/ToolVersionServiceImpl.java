package com.yagent.platform.service.tool.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yagent.platform.domain.tool.ToolVersionDO;
import com.yagent.platform.dto.tool.ToolVersionResponse;
import com.yagent.platform.exception.BizException;
import com.yagent.platform.mapper.tool.ToolVersionMapper;
import com.yagent.platform.service.tool.ToolVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToolVersionServiceImpl
        implements ToolVersionService {

    @Autowired
    private ToolVersionMapper toolVersionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ToolVersionResponse getVersion(
            String toolId,
            String version) {

        ToolVersionDO record =
                toolVersionMapper
                        .selectByToolIdAndVersion(
                                toolId,
                                version
                        );

        if (record == null) {

            throw new BizException(
                    "TOOL_VERSION_NOT_FOUND",
                    "Tool版本不存在"
            );
        }

        if (!"PUBLISHED".equals(
                record.getStatus())) {

            throw new BizException(
                    "TOOL_VERSION_UNAVAILABLE",
                    "Tool版本未发布"
            );
        }

        JsonNode manifest;

        try {

            manifest =
                    objectMapper.readTree(
                            record.getManifestJson()
                    );

        } catch (Exception e) {

            throw new BizException(
                    "INVALID_MANIFEST",
                    "manifest_json解析失败",
                    e
            );
        }

        ToolVersionResponse response =
                new ToolVersionResponse();

        response.setToolId(
                record.getToolId()
        );

        response.setVersion(
                record.getVersion()
        );

        response.setProtocolVersion(
                record.getProtocolVersion()
        );

        response.setRuntimeType(
                record.getRuntimeType()
        );

        response.setManifest(manifest);

        ToolVersionResponse.Artifact artifact =
                new ToolVersionResponse.Artifact();

        artifact.setSha256(
                record.getPackageSha256()
        );

        artifact.setSignature(
                record.getSignature()
        );

        response.setArtifact(artifact);

        return response;
    }
}
