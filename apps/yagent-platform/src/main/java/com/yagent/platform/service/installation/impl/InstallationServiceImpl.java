package com.yagent.platform.service.installation.impl;

import com.yagent.platform.domain.ToolInstallationDO;
import com.yagent.platform.domain.ToolVersionDO;
import com.yagent.platform.dto.installation.InstallationResolveRequest;
import com.yagent.platform.dto.installation.InstallationResolveResponse;
import com.yagent.platform.exception.BizException;
import com.yagent.platform.mapper.tool.ToolInstallationMapper;
import com.yagent.platform.mapper.tool.ToolVersionMapper;
import com.yagent.platform.service.installation.InstallationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class InstallationServiceImpl implements InstallationService {
    @Autowired
    private ToolInstallationMapper installationMapper;

    @Autowired
    private ToolVersionMapper toolVersionMapper;

    @Override
    public InstallationResolveResponse resolve(
            InstallationResolveRequest request) {

        if (request == null
                || request.getTenantId() == null) {

            throw new BizException(
                    "INVALID_ARGUMENT",
                    "tenantId cannot be empty"
            );
        }

        if (!StringUtils.hasText(
                request.getToolId())) {

            throw new BizException(
                    "INVALID_ARGUMENT",
                    "toolId cannot be empty"
            );
        }

        ToolInstallationDO installation =
                installationMapper
                        .selectByTenantAndTool(
                                request.getTenantId(),
                                request.getToolId()
                        );

        if (installation == null) {

            throw new BizException(
                    "TOOL_NOT_INSTALLED",
                    "Tool is not installed: "
                            + request.getToolId()
            );
        }

        if (!"ENABLED".equals(
                installation.getStatus())) {

            throw new BizException(
                    "TOOL_INSTALLATION_DISABLED",
                    "Tool installation is disabled"
            );
        }

        /*
         * 防止安装表指向了已经不存在/下架的版本。
         */
        ToolVersionDO version =
                toolVersionMapper
                        .selectByToolIdAndVersion(
                                installation.getToolId(),
                                installation.getVersion()
                        );

        if (version == null) {

            throw new BizException(
                    "TOOL_VERSION_NOT_FOUND",
                    "Installed tool version not found"
            );
        }

        if (!"PUBLISHED".equals(
                version.getStatus())) {

            throw new BizException(
                    "TOOL_VERSION_UNAVAILABLE",
                    "Installed tool version is unavailable"
            );
        }

        return new InstallationResolveResponse(
                installation.getToolId(),
                installation.getVersion()
        );
    }
}
