package com.yagent.platform.dto.permission;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class PermissionEvaluateResponse {

    private String decision;

    private List<String> reasons;

    public static PermissionEvaluateResponse allow() {

        PermissionEvaluateResponse result =
                new PermissionEvaluateResponse();

        result.setDecision("ALLOW");
        result.setReasons(Collections.emptyList());

        return result;
    }

    public static PermissionEvaluateResponse deny(
            String reason) {

        PermissionEvaluateResponse result =
                new PermissionEvaluateResponse();

        result.setDecision("DENY");

        List<String> reasons =
                new ArrayList<>();

        reasons.add(reason);

        result.setReasons(reasons);

        return result;
    }

    public static PermissionEvaluateResponse requireApproval(
            List<String> reasons) {

        PermissionEvaluateResponse result =
                new PermissionEvaluateResponse();

        result.setDecision(
                "REQUIRE_APPROVAL"
        );

        result.setReasons(reasons);

        return result;
    }
}
