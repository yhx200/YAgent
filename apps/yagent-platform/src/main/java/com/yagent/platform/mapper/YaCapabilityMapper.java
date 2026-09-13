package com.yagent.platform.mapper;

import com.yagent.platform.domain.YaCapability;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface YaCapabilityMapper {
    List<YaCapability> selectActive();
}
