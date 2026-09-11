package com.yagent.platform.service.capability.impl;

import com.yagent.platform.domain.capability.CapabilityProviderDO;
import com.yagent.platform.dto.capability.CapabilitySearchRequest;
import com.yagent.platform.dto.capability.CapabilitySearchResponse;
import com.yagent.platform.exception.BizException;
import com.yagent.platform.mapper.capability.CapabilityMapper;
import com.yagent.platform.service.capability.CapabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class CapabilityServiceImpl
        implements CapabilityService {

    @Autowired
    private CapabilityMapper capabilityMapper;

    @Override
    public CapabilitySearchResponse search(
            CapabilitySearchRequest request) {

        if (request == null
                || request.getTenantId() == null) {

            throw new BizException(
                    "INVALID_ARGUMENT",
                    "tenantId不能为空"
            );
        }

        if (!StringUtils.hasText(request.getQuery())) {

            throw new BizException(
                    "INVALID_ARGUMENT",
                    "query不能为空"
            );
        }

        List<CapabilityProviderDO> records =
                capabilityMapper.selectAvailableByTenant(
                        request.getTenantId()
                );

        Map<String,
                CapabilitySearchResponse.CapabilityItem>
                resultMap = new LinkedHashMap<>();

        for (CapabilityProviderDO record : records) {

            double score =
                    calculateScore(
                            request.getQuery(),
                            record
                    );

            if (score <= 0) {
                continue;
            }

            CapabilitySearchResponse.CapabilityItem item =
                    resultMap.get(
                            record.getCapabilityCode()
                    );

            if (item == null) {

                item =
                        new CapabilitySearchResponse
                                .CapabilityItem();

                item.setCapabilityCode(
                        record.getCapabilityCode()
                );

                item.setName(
                        record.getCapabilityName()
                );

                item.setScore(score);

                item.setProviders(
                        new ArrayList<>()
                );

                resultMap.put(
                        record.getCapabilityCode(),
                        item
                );

            } else if (score > item.getScore()) {

                item.setScore(score);
            }

            CapabilitySearchResponse.Provider provider =
                    new CapabilitySearchResponse.Provider();

            provider.setType("TOOL");

            provider.setToolId(
                    record.getToolId()
            );

            provider.setVersion(
                    record.getToolVersion()
            );

            provider.setToolName(
                    record.getToolName()
            );

            item.getProviders().add(provider);
        }

        List<CapabilitySearchResponse.CapabilityItem> items =
                new ArrayList<>(
                        resultMap.values()
                );

        Collections.sort(
                items,
                new Comparator<CapabilitySearchResponse.CapabilityItem>() {

                    @Override
                    public int compare(
                            CapabilitySearchResponse.CapabilityItem a,
                            CapabilitySearchResponse.CapabilityItem b) {

                        return Double.compare(
                                b.getScore(),
                                a.getScore()
                        );
                    }
                }
        );

        int limit =
                request.getLimit() == null
                        ? 5
                        : request.getLimit();

        if (limit <= 0) {
            limit = 5;
        }

        if (limit > 20) {
            limit = 20;
        }

        if (items.size() > limit) {

            items = new ArrayList<>(
                    items.subList(0, limit)
            );
        }

        CapabilitySearchResponse response =
                new CapabilitySearchResponse();

        response.setItems(items);

        return response;
    }

    private double calculateScore(
            String query,
            CapabilityProviderDO record) {

        double score = 0D;

        if (query.equals(
                record.getCapabilityCode())) {

            return 1D;
        }

        if (StringUtils.hasText(
                record.getCapabilityName())) {

            if (query.contains(
                    record.getCapabilityName())) {

                score = 0.95D;
            }
        }

        if (StringUtils.hasText(
                record.getKeywords())) {

            String[] keywords =
                    record.getKeywords().split(",");

            int count = 0;

            for (String keyword : keywords) {

                keyword = keyword.trim();

                if (keyword.length() > 0
                        && query.contains(keyword)) {

                    count++;
                }
            }

            if (count > 0) {

                score = Math.max(
                        score,
                        Math.min(
                                0.99D,
                                0.90D
                                        + count * 0.02D
                        )
                );
            }
        }

        return score;
    }
}
