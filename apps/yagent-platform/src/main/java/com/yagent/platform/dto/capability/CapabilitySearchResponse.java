package com.yagent.platform.dto.capability;

import lombok.Data;

import java.util.List;

@Data
public class CapabilitySearchResponse {

    private List<CapabilityItem> items;

    @Data
    public static class CapabilityItem {

        private String capabilityCode;

        private String name;

        private Double score;

        private List<Provider> providers;
    }

    @Data
    public static class Provider {

        private String type;

        private String toolId;

        private String version;

        private String toolName;
    }
}
