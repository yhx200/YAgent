package com.yagent.platform.mapper;
import com.yagent.platform.domain.YaTenantToolPolicy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper public interface YaTenantToolPolicyMapper {
    YaTenantToolPolicy selectAccessPolicy(@Param("tenantId") Long tenantId,@Param("toolId") String toolId);
}
