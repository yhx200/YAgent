package com.yagent.platform.mapper;

import com.yagent.platform.domain.YaTenantInstallation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface YaTenantInstallationMapper {
    List<YaTenantInstallation> selectInstalled(@Param("tenantId") Long tenantId, @Param("toolIds") List<String> toolIds);

    YaTenantInstallation selectOneInstalled(@Param("tenantId") Long tenantId, @Param("toolId") String toolId);
}
