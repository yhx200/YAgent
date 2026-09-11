package com.yagent.platform.mapper.capability;

import com.yagent.platform.domain.capability.CapabilityProviderDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CapabilityMapper {

    List<CapabilityProviderDO> selectAvailableByTenant(
            @Param("tenantId") Long tenantId
    );
}
