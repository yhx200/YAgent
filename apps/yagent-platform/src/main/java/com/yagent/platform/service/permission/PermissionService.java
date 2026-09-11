package com.yagent.platform.service.permission;

import com.yagent.platform.dto.permission.PermissionEvaluateRequest;
import com.yagent.platform.dto.permission.PermissionEvaluateResponse;

public interface PermissionService {

    PermissionEvaluateResponse evaluate(PermissionEvaluateRequest request);

}
