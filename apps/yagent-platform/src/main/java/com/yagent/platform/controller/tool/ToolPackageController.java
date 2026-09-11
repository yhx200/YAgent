package com.yagent.platform.controller.tool;

import com.yagent.platform.dto.packageinfo.ToolPackageTicketRequest;
import com.yagent.platform.dto.packageinfo.ToolPackageTicketResponse;
import com.yagent.platform.service.tool.ToolPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inner/v1/tool-packages")
public class ToolPackageController {

    @Autowired
    private ToolPackageService toolPackageService;

    @PostMapping("/download-ticket")
    public ToolPackageTicketResponse downloadTicket(
            @RequestBody ToolPackageTicketRequest request) {

        return toolPackageService.createDownloadTicket(
                request
        );
    }
}
