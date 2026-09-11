package com.yagent.platform.service.tool.impl;

import com.yagent.platform.domain.ToolInstallationDO;
import com.yagent.platform.domain.ToolVersionDO;
import com.yagent.platform.dto.packageinfo.ToolPackageTicketRequest;
import com.yagent.platform.dto.packageinfo.ToolPackageTicketResponse;
import com.yagent.platform.exception.BizException;
import com.yagent.platform.mapper.tool.ToolInstallationMapper;
import com.yagent.platform.mapper.tool.ToolVersionMapper;
import com.yagent.platform.service.tool.ToolPackageService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class ToolPackageServiceImpl implements ToolPackageService {
    /**
     * 下载地址有效期：5分钟
     */
    private static final int DOWNLOAD_EXPIRES_SECONDS =
            300;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private ToolVersionMapper toolVersionMapper;

    @Autowired
    private ToolInstallationMapper installationMapper;

    @Override
    public ToolPackageTicketResponse createDownloadTicket(
            ToolPackageTicketRequest request) {

        validateRequest(request);

        /*
         * 1. 确认租户安装了这个 Tool。
         */
        ToolInstallationDO installation =
                installationMapper
                        .selectByTenantAndTool(
                                request.getTenantId(),
                                request.getToolId()
                        );

        if (installation == null) {

            throw new BizException(
                    "TOOL_NOT_INSTALLED",
                    "Tool is not installed"
            );
        }

        /*
         * 2. 安装必须处于 ENABLED。
         */
        if (!"ENABLED".equals(
                installation.getStatus())) {

            throw new BizException(
                    "TOOL_INSTALLATION_DISABLED",
                    "Tool installation is disabled"
            );
        }

        /*
         * 3. 非常重要：
         *
         * Runtime 不允许自己传一个任意版本过来下载。
         *
         * 必须与当前租户安装版本一致。
         */
        if (!request.getVersion().equals(
                installation.getVersion())) {

            throw new BizException(
                    "TOOL_VERSION_NOT_ALLOWED",
                    "Requested tool version is not installed"
            );
        }

        /*
         * 4. 查询 Tool Version。
         */
        ToolVersionDO toolVersion =
                toolVersionMapper
                        .selectByToolIdAndVersion(
                                request.getToolId(),
                                request.getVersion()
                        );

        if (toolVersion == null) {

            throw new BizException(
                    "TOOL_VERSION_NOT_FOUND",
                    "Tool version not found"
            );
        }

        if (!"PUBLISHED".equals(
                toolVersion.getStatus())) {

            throw new BizException(
                    "TOOL_VERSION_UNAVAILABLE",
                    "Tool version unavailable"
            );
        }

        /*
         * 5. 生成 MinIO 短期签名 URL。
         */
        String downloadUrl;

        try {

            downloadUrl =
                    minioClient
                            .getPresignedObjectUrl(
                                    GetPresignedObjectUrlArgs
                                            .builder()
                                            .method(Method.GET)
                                            .bucket(
                                                    toolVersion
                                                            .getBucket()
                                            )
                                            .object(
                                                    toolVersion
                                                            .getObjectKey()
                                            )
                                            .expiry(
                                                    DOWNLOAD_EXPIRES_SECONDS,
                                                    TimeUnit.SECONDS
                                            )
                                            .build()
                            );

        } catch (Exception e) {

            throw new BizException(
                    "CREATE_DOWNLOAD_TICKET_FAILED",
                    "Failed to create tool package download ticket",
                    e
            );
        }

        ToolPackageTicketResponse response =
                new ToolPackageTicketResponse();

        response.setDownloadUrl(downloadUrl);

        response.setExpiresIn(
                DOWNLOAD_EXPIRES_SECONDS
        );

        response.setSha256(
                toolVersion.getSha256()
        );

        response.setSignature(
                toolVersion.getSignature()
        );

        return response;
    }


    private void validateRequest(
            ToolPackageTicketRequest request) {

        if (request == null) {

            throw new BizException(
                    "INVALID_ARGUMENT",
                    "request cannot be null"
            );
        }

        if (request.getTenantId() == null) {

            throw new BizException(
                    "INVALID_ARGUMENT",
                    "tenantId cannot be empty"
            );
        }

        if (!StringUtils.hasText(
                request.getToolId())) {

            throw new BizException(
                    "INVALID_ARGUMENT",
                    "toolId cannot be empty"
            );
        }

        if (!StringUtils.hasText(
                request.getVersion())) {

            throw new BizException(
                    "INVALID_ARGUMENT",
                    "version cannot be empty"
            );
        }
    }
}
