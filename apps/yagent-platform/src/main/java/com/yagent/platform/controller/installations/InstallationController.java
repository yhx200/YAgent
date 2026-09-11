package com.yagent.platform.controller.installation;

import com.yagent.platform.dto.installation.InstallationResolveRequest;
import com.yagent.platform.dto.installation.InstallationResolveResponse;
import com.yagent.platform.service.installation.InstallationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner/v1/installations")
public class InstallationController {

    @Autowired
    private InstallationService installationService;

    @PostMapping("/resolve")
    public InstallationResolveResponse resolve(
            @RequestBody InstallationResolveRequest request) {

        return installationService.resolve(request);
    }
}
