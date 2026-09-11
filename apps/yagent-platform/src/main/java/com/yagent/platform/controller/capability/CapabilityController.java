package com.yagent.platform.controller.capability;

import com.yagent.platform.dto.capability.CapabilitySearchRequest;
import com.yagent.platform.dto.capability.CapabilitySearchResponse;
import com.yagent.platform.service.capability.CapabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner/v1/capabilities")
public class CapabilityController {

    @Autowired
    private CapabilityService capabilityService;

    @PostMapping("/search")
    public CapabilitySearchResponse search(
            @RequestBody CapabilitySearchRequest request) {

        return capabilityService.search(request);
    }
}
