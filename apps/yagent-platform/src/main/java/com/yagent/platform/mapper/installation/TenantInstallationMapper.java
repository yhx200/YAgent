package com.yagent.platform.mapper.installation;

import com.yagent.platform.domain.installation.TenantInstallationDO;
import org.apache.ibatis.annotations.Param;

public interface TenantInstallationMapper {

    TenantInstallationDO selectByTenantAndTool(
            @Param("tenantId") Long tenantId,
            @Param("toolId") String toolId
    );
}
