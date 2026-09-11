package com.yagent.platform.service.installation.impl;

import com.yagent.platform.domain.installation.TenantInstallationDO;
import com.yagent.platform.domain.tool.ToolVersionDO;
import com.yagent.platform.dto.installation.InstallationResolveRequest;
import com.yagent.platform.dto.installation.InstallationResolveResponse;
import com.yagent.platform.exception.BizException;
import com.yagent.platform.mapper.installation.TenantInstallationMapper;
import com.yagent.platform.mapper.tool.ToolVersionMapper;
import com.yagent.platform.service.installation.InstallationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InstallationServiceImpl
        implements InstallationService {

    @Autowired
    private TenantInstallationMapper installationMapper;

    @Autowired
    private ToolVersionMapper toolVersionMapper;

    @Override
    public InstallationResolveResponse resolve(
            InstallationResolveRequest request) {

        if (request == null
                || request.getTenantId() == null) {

            throw new BizException(
                    "INVALID_ARGUMENT",
                    "tenantId不能为空"
            );
        }

        TenantInstallationDO installation =
                installationMapper
                        .selectByTenantAndTool(
                                request.getTenantId(),
                                request.getToolId()
                        );

        if (installation == null) {

            throw new BizException(
                    "TOOL_NOT_INSTALLED",
                    "租户未安装该Tool"
            );
        }

        if (!"ACTIVE".equals(
                installation.getStatus())) {

            throw new BizException(
                    "INSTALLATION_DISABLED",
                    "Tool安装已停用"
            );
        }

        ToolVersionDO version =
                toolVersionMapper
                        .selectByToolIdAndVersion(
                                installation.getToolId(),
                                installation.getVersion()
                        );

        if (version == null
                || !"PUBLISHED".equals(
                version.getStatus())) {

            throw new BizException(
                    "VERSION_UNAVAILABLE",
                    "安装版本不可用"
            );
        }

        return new InstallationResolveResponse(
                installation.getToolId(),
                installation.getVersion()
        );
    }
}
