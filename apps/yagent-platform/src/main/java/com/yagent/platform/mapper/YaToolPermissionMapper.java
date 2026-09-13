package com.yagent.platform.mapper;

import com.yagent.platform.domain.YaToolPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface YaToolPermissionMapper {
    List<YaToolPermission> selectByToolVersion(@Param("toolId") String toolId, @Param("version") String version);
}
