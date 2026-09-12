package com.yagent.platform.dto;
import lombok.Data;
import java.util.*;
@Data public class ToolManifest {
  private String schemaVersion; private String id; private String version; private String name; private String displayName; private String description; private Runtime runtime; private List<Capability> capabilities; private List<ToolFunction> tools; private Permissions permissions; private Limits limits;
  @Data public static class Runtime { private String type; private String entry; }
  @Data public static class Capability { private String code; private String tool; }
  @Data public static class ToolFunction { private String name; private String description; private Map<String,Object> inputSchema; private Map<String,Object> outputSchema; }
  @Data public static class Permissions { private Network network; private Filesystem filesystem; private Boolean shell; }
  @Data public static class Network { private Boolean enabled; private List<String> allow; }
  @Data public static class Filesystem { private List<String> read; private List<String> write; }
  @Data public static class Limits { private Integer timeoutMs; private Integer memoryMb; }
}
