package com.yagent.platform.mapper.tool;

import com.yagent.platform.domain.tool.ToolVersionDO;
import org.apache.ibatis.annotations.Param;

public interface ToolVersionMapper {

    ToolVersionDO selectByToolIdAndVersion(
            @Param("toolId") String toolId,
            @Param("version") String version
    );
}
