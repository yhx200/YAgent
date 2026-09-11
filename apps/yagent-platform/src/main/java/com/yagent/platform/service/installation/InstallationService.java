package com.yagent.platform.service.installation;

import com.yagent.platform.dto.installation.InstallationResolveRequest;
import com.yagent.platform.dto.installation.InstallationResolveResponse;

public interface InstallationService {

    InstallationResolveResponse resolve(
            InstallationResolveRequest request
    );
}
