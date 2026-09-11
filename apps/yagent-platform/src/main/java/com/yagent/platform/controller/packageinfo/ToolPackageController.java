package com.yagent.platform.controller.packageinfo;

import com.yagent.platform.dto.tool.ToolPackageTicketRequest;
import com.yagent.platform.dto.tool.ToolPackageTicketResponse;
import com.yagent.platform.service.tool.ToolPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner/v1/tool-packages")
public class ToolPackageController {

    @Autowired
    private ToolPackageService toolPackageService;

    @PostMapping("/download-ticket")
    public ToolPackageTicketResponse downloadTicket(
            @RequestBody ToolPackageTicketRequest request) {

        return toolPackageService
                .createDownloadTicket(request);
    }
}
