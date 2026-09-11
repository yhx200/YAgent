package com.yagent.platform.service.tool;

import com.yagent.platform.dto.tool.ToolVersionResponse;

public interface ToolVersionService {

    ToolVersionResponse getVersion(
            String toolId,
            String version
    );
}
