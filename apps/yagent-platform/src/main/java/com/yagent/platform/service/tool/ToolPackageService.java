package com.yagent.platform.service.tool;

import com.yagent.platform.dto.tool.ToolPackageTicketRequest;
import com.yagent.platform.dto.tool.ToolPackageTicketResponse;

public interface ToolPackageService {

    ToolPackageTicketResponse createDownloadTicket(
            ToolPackageTicketRequest request
    );
}
