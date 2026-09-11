package com.yagent.platform.mapper.tool;

import com.yagent.platform.domain.ToolVersionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ToolVersionMapper {

    ToolVersionDO selectByToolIdAndVersion(
            @Param("toolId") String toolId,
            @Param("version") String version
    );
}
