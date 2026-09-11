package com.yagent.platform.mapper.tool;

import com.yagent.platform.domain.ToolInstallationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ToolInstallationMapper {

    ToolInstallationDO selectByTenantAndTool(
            @Param("tenantId") Long tenantId,
            @Param("toolId") String toolId
    );
}
