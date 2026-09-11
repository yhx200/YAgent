package com.yagent.platform.service.permission.impl;

import com.yagent.platform.domain.installation.TenantInstallationDO;
import com.yagent.platform.domain.permission.TenantToolPolicyDO;
import com.yagent.platform.domain.permission.ToolPermissionDO;
import com.yagent.platform.dto.permission.PermissionEvaluateRequest;
import com.yagent.platform.dto.permission.PermissionEvaluateResponse;
import com.yagent.platform.mapper.installation.TenantInstallationMapper;
import com.yagent.platform.mapper.permission.TenantToolPolicyMapper;
import com.yagent.platform.mapper.permission.ToolPermissionMapper;
import com.yagent.platform.service.permission.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PermissionServiceImpl
        implements PermissionService {

    @Autowired
    private ToolPermissionMapper toolPermissionMapper;

    @Autowired
    private TenantToolPolicyMapper tenantToolPolicyMapper;

    @Autowired
    private TenantInstallationMapper installationMapper;

    @Override
    public PermissionEvaluateResponse evaluate(
            PermissionEvaluateRequest request) {

        if (request == null
                || request.getTenantId() == null) {

            return PermissionEvaluateResponse.deny(
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

            return PermissionEvaluateResponse.deny(
                    "Tool未安装"
            );
        }

        if (!"ACTIVE".equals(
                installation.getStatus())) {

            return PermissionEvaluateResponse.deny(
                    "Tool安装已停用"
            );
        }

        if (!installation
                .getVersion()
                .equals(request.getVersion())) {

            return PermissionEvaluateResponse.deny(
                    "请求版本不是当前安装版本"
            );
        }

        List<ToolPermissionDO> permissions =
                toolPermissionMapper
                        .selectByToolVersion(
                                request.getToolId(),
                                request.getVersion()
                        );

        /*
         * Tool没有声明任何特殊权限。
         */
        if (permissions == null
                || permissions.isEmpty()) {

            return PermissionEvaluateResponse.allow();
        }

        List<TenantToolPolicyDO> policies =
                tenantToolPolicyMapper
                        .selectActivePolicies(
                                request.getTenantId(),
                                request.getToolId()
                        );

        /*
         * key:
         * NETWORK
         *
         * value:
         * ALLOW / DENY
         */
        Map<String, String> policyMap =
                new HashMap<>();

        if (policies != null) {

            for (TenantToolPolicyDO policy : policies) {

                /*
                 * SQL里 Tool 专属策略排在前面。
                 *
                 * 所以第一次出现就保留。
                 */
                if (!policyMap.containsKey(
                        policy.getPolicyType())) {

                    policyMap.put(
                            policy.getPolicyType(),
                            policy.getPolicyValue()
                    );
                }
            }
        }

        List<String> approvalReasons =
                new ArrayList<>();

        for (ToolPermissionDO permission
                : permissions) {

            String policy =
                    policyMap.get(
                            permission.getPermissionType()
                    );

            if ("DENY".equalsIgnoreCase(policy)) {

                return PermissionEvaluateResponse.deny(
                        "租户禁止权限："
                                + permission.getPermissionType()
                );
            }

            if ("ALLOW".equalsIgnoreCase(policy)) {
                continue;
            }

            approvalReasons.add(
                    "Tool requests "
                            + permission.getPermissionType()
                            + ": "
                            + permission.getPermissionValue()
            );
        }

        if (!approvalReasons.isEmpty()) {

            return PermissionEvaluateResponse
                    .requireApproval(
                            approvalReasons
                    );
        }

        return PermissionEvaluateResponse.allow();
    }
}
