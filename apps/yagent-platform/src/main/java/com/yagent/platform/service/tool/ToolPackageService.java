package com.yagent.platform.service.tool;

import com.yagent.platform.dto.packageinfo.ToolPackageTicketRequest;
import com.yagent.platform.dto.packageinfo.ToolPackageTicketResponse;

public interface ToolPackageService {

    ToolPackageTicketResponse createDownloadTicket(ToolPackageTicketRequest request);

}
