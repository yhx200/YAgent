package com.yagent.platform.service.capability;

import com.yagent.platform.dto.capability.CapabilitySearchRequest;
import com.yagent.platform.dto.capability.CapabilitySearchResponse;

public interface CapabilityService {

     CapabilitySearchResponse search(
             CapabilitySearchRequest request
     );
}
