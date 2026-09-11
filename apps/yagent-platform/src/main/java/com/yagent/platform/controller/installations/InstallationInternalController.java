package com.yagent.platform.controller.installation;

import com.yagent.platform.dto.installation.InstallationResolveRequest;
import com.yagent.platform.dto.installation.InstallationResolveResponse;
import com.yagent.platform.service.installation.InstallationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inner/v1/installations")
public class InstallationInternalController {

    @Autowired
    private InstallationService installationService;

    @PostMapping("/resolve")
    public InstallationResolveResponse resolve(
            @RequestBody InstallationResolveRequest request) {

        return installationService.resolve(request);
    }
}
