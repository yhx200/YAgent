package com.yagent.platform.dto.installation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallationResolveResponse {

    private String toolId;

    private String version;
}
