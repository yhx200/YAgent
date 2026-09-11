package com.yagent.platform.service.permission.impl;

import com.yagent.platform.domain.ToolInstallationDO;
import com.yagent.platform.domain.ToolVersionDO;
import com.yagent.platform.dto.permission.PermissionEvaluateRequest;
import com.yagent.platform.dto.permission.PermissionEvaluateResponse;
import com.yagent.platform.mapper.tool.ToolInstallationMapper;
import com.yagent.platform.mapper.tool.ToolVersionMapper;
import com.yagent.platform.service.permission.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private ToolInstallationMapper installationMapper;

    @Autowired
    private ToolVersionMapper toolVersionMapper;


    public PermissionEvaluateResponse evaluate(
            PermissionEvaluateRequest request) {

        if (request == null
                || request.getTenantId() == null) {

            return PermissionEvaluateResponse.deny(
                    "tenantId is required"
            );
        }

        if (request.getToolId() == null
                || request.getToolId().trim().isEmpty()) {

            return PermissionEvaluateResponse.deny(
                    "toolId is required"
            );
        }

        if (request.getVersion() == null
                || request.getVersion().trim().isEmpty()) {

            return PermissionEvaluateResponse.deny(
                    "version is required"
            );
        }

        ToolInstallationDO installation =
                installationMapper
                        .selectByTenantAndTool(
                                request.getTenantId(),
                                request.getToolId()
                        );

        if (installation == null) {

            return PermissionEvaluateResponse.deny(
                    "Tool is not installed"
            );
        }

        if (!"ENABLED".equals(
                installation.getStatus())) {

            return PermissionEvaluateResponse.deny(
                    "Tool installation is disabled"
            );
        }

        if (!request.getVersion().equals(
                installation.getVersion())) {

            return PermissionEvaluateResponse.deny(
                    "Requested version is not installed"
            );
        }

        ToolVersionDO toolVersion =
                toolVersionMapper
                        .selectByToolIdAndVersion(
                                request.getToolId(),
                                request.getVersion()
                        );

        if (toolVersion == null) {

            return PermissionEvaluateResponse.deny(
                    "Tool version does not exist"
            );
        }

        if (!"PUBLISHED".equals(
                toolVersion.getStatus())) {

            return PermissionEvaluateResponse.deny(
                    "Tool version is unavailable"
            );
        }

        /*
         *
         * V1 到这里直接 ALLOW。
         *
         * todo:后面再解析 manifest：
         *
         * permissions.network
         * permissions.filesystem
         * permissions.shell
         * permissions.secret
         *
         * 再判断是否：
         *
         * ALLOW
         * REQUIRE_APPROVAL
         * DENY
         *
         * if (requiresNetwork
         *      && !userApprovedNetwork) {
         *      return PermissionEvaluateResponse
         *          .requireApproval(
         *              "Tool requests external network access"
         *          );
         *      }
         */

        return PermissionEvaluateResponse.allow();
    }

}
