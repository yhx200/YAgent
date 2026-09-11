package com.yagent.platform.service.tool.impl;

import com.yagent.platform.domain.installation.TenantInstallationDO;
import com.yagent.platform.domain.tool.ToolVersionDO;
import com.yagent.platform.dto.tool.ToolPackageTicketRequest;
import com.yagent.platform.dto.tool.ToolPackageTicketResponse;
import com.yagent.platform.exception.BizException;
import com.yagent.platform.mapper.installation.TenantInstallationMapper;
import com.yagent.platform.mapper.tool.ToolVersionMapper;
import com.yagent.platform.service.tool.ToolPackageService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ToolPackageServiceImpl
        implements ToolPackageService {

    private static final int EXPIRES_SECONDS = 300;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private TenantInstallationMapper installationMapper;

    @Autowired
    private ToolVersionMapper toolVersionMapper;

    @Value("${yagent.minio.bucket}")
    private String bucket;

    @Override
    public ToolPackageTicketResponse createDownloadTicket(
            ToolPackageTicketRequest request) {

        TenantInstallationDO installation =
                installationMapper
                        .selectByTenantAndTool(
                                request.getTenantId(),
                                request.getToolId()
                        );

        if (installation == null) {

            throw new BizException(
                    "TOOL_NOT_INSTALLED",
                    "租户未安装此Tool"
            );
        }

        if (!"ACTIVE".equals(
                installation.getStatus())) {

            throw new BizException(
                    "INSTALLATION_DISABLED",
                    "Tool已停用"
            );
        }

        if (!installation
                .getVersion()
                .equals(request.getVersion())) {

            throw new BizException(
                    "VERSION_NOT_ALLOWED",
                    "请求版本不是租户安装版本"
            );
        }

        ToolVersionDO version =
                toolVersionMapper
                        .selectByToolIdAndVersion(
                                request.getToolId(),
                                request.getVersion()
                        );

        if (version == null
                || !"PUBLISHED".equals(
                version.getStatus())) {

            throw new BizException(
                    "VERSION_UNAVAILABLE",
                    "Tool版本不可用"
            );
        }

        try {

            String url =
                    minioClient
                            .getPresignedObjectUrl(
                                    GetPresignedObjectUrlArgs
                                            .builder()
                                            .method(Method.GET)
                                            .bucket(bucket)
                                            .object(
                                                    version
                                                            .getPackageObjectKey()
                                            )
                                            .expiry(
                                                    EXPIRES_SECONDS,
                                                    TimeUnit.SECONDS
                                            )
                                            .build()
                            );

            ToolPackageTicketResponse response =
                    new ToolPackageTicketResponse();

            response.setDownloadUrl(url);

            response.setExpiresIn(
                    EXPIRES_SECONDS
            );

            response.setSha256(
                    version.getPackageSha256()
            );

            response.setSignature(
                    version.getSignature()
            );

            return response;

        } catch (Exception e) {

            throw new BizException(
                    "MINIO_ERROR",
                    "生成Tool下载地址失败",
                    e
            );
        }
    }
}
