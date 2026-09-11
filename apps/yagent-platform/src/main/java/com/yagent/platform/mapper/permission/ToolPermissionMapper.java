package com.yagent.platform.mapper.permission;

import com.yagent.platform.domain.permission.ToolPermissionDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ToolPermissionMapper {

    List<ToolPermissionDO> selectByToolVersion(
            @Param("toolId") String toolId,
            @Param("version") String version
    );
}
