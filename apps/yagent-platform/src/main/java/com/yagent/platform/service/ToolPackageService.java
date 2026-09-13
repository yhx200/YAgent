package com.yagent.platform.service;

import com.yagent.platform.domain.YaToolVersion;
import com.yagent.platform.dto.RuntimeDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ToolPackageService {
    private final RuntimePlatformService runtimeService;
    @Value("${yagent.internal-base-url}")
    private String baseUrl;
    @Value("${yagent.tool-storage-root}")
    private String storageRoot;

    public DownloadTicketResponse ticket(DownloadTicketRequest request) {
        YaToolVersion v = runtimeService.version(request.getToolId(), request.getVersion());
        if (v == null) throw new IllegalArgumentException("Tool version not found");
        DownloadTicketResponse r = new DownloadTicketResponse();
        r.setObjectKey(v.getPackageObjectKey());
        r.setSha256(v.getPackageSha256());
        r.setSignature(v.getSignature());
        r.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        r.setUrl(baseUrl + "/inner/v1/tool-packages/file?objectKey=" + URLEncoder.encode(v.getPackageObjectKey(), StandardCharsets.UTF_8));
        return r;
    }

    public Resource file(String objectKey) {
        Path root = Path.of(storageRoot).toAbsolutePath().normalize();
        Path file = root.resolve(objectKey).normalize();
        if (!file.startsWith(root)) throw new IllegalArgumentException("Invalid objectKey");
        if (!Files.isRegularFile(file)) throw new IllegalArgumentException("Package not found: " + objectKey);
        return new FileSystemResource(file);
    }
}
