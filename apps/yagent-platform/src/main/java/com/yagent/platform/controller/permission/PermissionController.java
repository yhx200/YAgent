package com.yagent.platform.controller.permission;

import com.yagent.platform.dto.permission.PermissionEvaluateRequest;
import com.yagent.platform.dto.permission.PermissionEvaluateResponse;
import com.yagent.platform.service.permission.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner/v1/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/evaluate")
    public PermissionEvaluateResponse evaluate(
            @RequestBody PermissionEvaluateRequest request) {

        return permissionService.evaluate(request);
    }
}
