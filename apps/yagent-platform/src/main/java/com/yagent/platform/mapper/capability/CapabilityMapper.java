package com.yagent.platform.mapper.capability;

import com.yagent.platform.domain.CapabilityDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CapabilityMapper {

    /**
     * 查询全部启用能力。
     *
     * V1 数量很少，先由 Java 做简单匹配。
     */
    List<CapabilityDO> selectAllEnabled();
}
