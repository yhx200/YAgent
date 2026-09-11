package com.yagent.platform.controller.tool;

import com.yagent.platform.dto.tool.ToolVersionResponse;
import com.yagent.platform.service.tool.ToolVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner/v1/tools")
public class ToolController {

    @Autowired
    private ToolVersionService toolVersionService;

    @GetMapping("/{toolId}/versions/{version}")
    public ToolVersionResponse getVersion(
            @PathVariable String toolId,
            @PathVariable String version) {

        return toolVersionService.getVersion(
                toolId,
                version
        );
    }
}
