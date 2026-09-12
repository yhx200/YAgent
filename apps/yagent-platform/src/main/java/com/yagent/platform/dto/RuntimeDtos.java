package com.yagent.platform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.*;

public final class RuntimeDtos {
  private RuntimeDtos(){}

  @Data public static class CapabilitySearchRequest { @NotNull private Long tenantId; @NotBlank private String query; private Integer limit=5; }
  @Data public static class CapabilityProvider { private String type; private String toolId; private String version; private String toolName; private boolean installed; }
  @Data public static class CapabilityItem { private String capabilityCode; private String name; private String description; private String category; private double score; private List<CapabilityProvider> providers=new ArrayList<>(); }
  @Data public static class CapabilitySearchResponse { private List<CapabilityItem> items=new ArrayList<>(); }

  @Data public static class InstallationResolveRequest { @NotNull private Long tenantId; @NotBlank private String capabilityCode; }
  @Data public static class PackageInfo { private String objectKey; private String sha256; private String signature; }
  @Data public static class ToolPermission { private String type; private String value; }
  @Data public static class ResolvedTool { private String toolId; private String version; private String toolName; private String displayName; private String description; private String runtimeType; private String protocolVersion; private String entrypoint; private Map<String,Object> inputSchema; private Integer timeoutMs=10000; private PackageInfo packageInfo; private List<ToolPermission> permissions=new ArrayList<>(); }
  @Data public static class InstallationResolveResponse { private String capabilityCode; private List<ResolvedTool> tools=new ArrayList<>(); }

  @Data public static class PermissionEvaluateRequest { @NotNull private Long tenantId; @NotNull private Long userId; @NotBlank private String sessionId; @NotBlank private String toolId; @NotBlank private String version; @NotBlank private String toolName; @NotBlank private String stage; }
  @Data public static class PermissionEvaluateResponse { private String decision; private List<String> reasons=new ArrayList<>(); }

  @Data public static class DownloadTicketRequest { @NotBlank private String toolId; @NotBlank private String version; }
  @Data public static class DownloadTicketResponse { private String url; private LocalDateTime expiresAt; private String sha256; private String signature; private String objectKey; }

  @Data public static class AuditEventRequest { private String traceId; private Long tenantId; private Long userId; private String sessionId; private String eventType; private String capabilityCode; private String toolId; private String toolVersion; private String toolName; private String resultStatus; private Long durationMs; private String detailJson; }
}
