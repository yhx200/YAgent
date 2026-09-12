package com.yagent.platform.controller;

import com.yagent.platform.dto.RuntimeDtos.*;
import com.yagent.platform.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner/v1")
@RequiredArgsConstructor
public class RuntimeInternalController {
    private final RuntimePlatformService service;
    private final ToolPackageService packages;

    @PostMapping("/capabilities/search")
    public CapabilitySearchResponse search(@Valid @RequestBody CapabilitySearchRequest r) {
        return service.search(r);
    }

    @PostMapping("/installations/resolve")
    public InstallationResolveResponse resolve(@Valid @RequestBody InstallationResolveRequest r) {
        return service.resolve(r);
    }

    @PostMapping("/permissions/evaluate")
    public PermissionEvaluateResponse permission(@Valid @RequestBody PermissionEvaluateRequest r) {
        return service.evaluate(r);
    }

    @PostMapping("/audit-events")
    public java.util.Map<String, Object> audit(@RequestBody AuditEventRequest r) {
        service.audit(r);
        return java.util.Map.of("success", true);
    }

    @PostMapping("/tool-packages/download-ticket")
    public DownloadTicketResponse ticket(@Valid @RequestBody DownloadTicketRequest r) {
        return packages.ticket(r);
    }

    @GetMapping("/tool-packages/file")
    public ResponseEntity<Resource> file(@RequestParam String objectKey) {
        Resource resource = packages.file(objectKey);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=package.ytool").body(resource);
    }

    @GetMapping("/tools/{toolId}/versions/{version}")
    public Object version(@PathVariable String toolId, @PathVariable String version) {
        var v = service.version(toolId, version);
        if (v == null) throw new IllegalArgumentException("Tool version not found");
        return v;
    }
}
