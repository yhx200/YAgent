package com.yagent.platform.dto.permission;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class PermissionEvaluateResponse {

    /**
     * ALLOW
     * REQUIRE_APPROVAL
     * DENY
     */
    private String decision;

    private List<String> reasons;

    public static PermissionEvaluateResponse allow() {

        PermissionEvaluateResponse response =
                new PermissionEvaluateResponse();

        response.setDecision("ALLOW");
        response.setReasons(Collections.<String>emptyList());

        return response;
    }

    public static PermissionEvaluateResponse deny(String reason) {

        PermissionEvaluateResponse response =
                new PermissionEvaluateResponse();

        response.setDecision("DENY");

        List<String> reasons = new ArrayList<String>();
        reasons.add(reason);

        response.setReasons(reasons);

        return response;
    }

    public static PermissionEvaluateResponse requireApproval(
            String reason) {

        PermissionEvaluateResponse response =
                new PermissionEvaluateResponse();

        response.setDecision("REQUIRE_APPROVAL");

        List<String> reasons = new ArrayList<String>();
        reasons.add(reason);

        response.setReasons(reasons);

        return response;
    }
}
