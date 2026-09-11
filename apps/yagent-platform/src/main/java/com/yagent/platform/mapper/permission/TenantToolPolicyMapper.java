package com.yagent.platform.mapper.permission;

import com.yagent.platform.domain.permission.TenantToolPolicyDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TenantToolPolicyMapper {

    List<TenantToolPolicyDO> selectActivePolicies(
            @Param("tenantId") Long tenantId,
            @Param("toolId") String toolId
    );
}
