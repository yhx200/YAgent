package com.yagent.platform.service.capability.impl;

import com.yagent.platform.domain.CapabilityDO;
import com.yagent.platform.dto.capability.CapabilitySearchRequest;
import com.yagent.platform.dto.capability.CapabilitySearchResponse;
import com.yagent.platform.mapper.capability.CapabilityMapper;
import com.yagent.platform.service.capability.CapabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public abstract class CapabilityServiceImpl implements CapabilityService {

    @Autowired
    private CapabilityMapper capabilityMapper;

    @Override
    public CapabilitySearchResponse search(
            CapabilitySearchRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "request cannot be null"
            );
        }

        if (!StringUtils.hasText(request.getQuery())) {
            throw new IllegalArgumentException(
                    "query cannot be empty"
            );
        }

        int limit = request.getLimit() == null
                ? 5
                : request.getLimit();

        if (limit <= 0) {
            limit = 5;
        }

        if (limit > 20) {
            limit = 20;
        }

        List<CapabilityDO> capabilityList =
                capabilityMapper.selectAllEnabled();

        /*
         * 一个 capabilityCode 未来可能有多个 Provider。
         *
         * 所以不能简单一行数据库 = 一个返回 item。
         */
        Map<String, CapabilitySearchResponse.CapabilityItem>
                itemMap = new LinkedHashMap<String,
                                CapabilitySearchResponse.CapabilityItem>();

        String query = request.getQuery().trim();

        for (CapabilityDO capability : capabilityList) {

            double score = calculateScore(
                    query,
                    capability
            );

            if (score <= 0) {
                continue;
            }

            CapabilitySearchResponse.CapabilityItem item =
                    itemMap.get(
                            capability.getCapabilityCode()
                    );

            if (item == null) {

                item =
                        new CapabilitySearchResponse
                                .CapabilityItem();

                item.setCapabilityCode(
                        capability.getCapabilityCode()
                );

                item.setCapabilityName(
                        capability.getCapabilityName()
                );

                item.setScore(score);

                item.setProviders(
                        new ArrayList<
                                CapabilitySearchResponse.Provider>()
                );

                itemMap.put(
                        capability.getCapabilityCode(),
                        item
                );
            } else {

                if (score > item.getScore()) {
                    item.setScore(score);
                }
            }

            CapabilitySearchResponse.Provider provider =
                    new CapabilitySearchResponse.Provider();

            provider.setType(
                    capability.getProviderType()
            );

            provider.setToolId(
                    capability.getToolId()
            );

            item.getProviders().add(provider);
        }

        List<CapabilitySearchResponse.CapabilityItem> items =
                new ArrayList<
                        CapabilitySearchResponse.CapabilityItem>(
                        itemMap.values()
                );

        Collections.sort(
                items,
                new Comparator<
                        CapabilitySearchResponse.CapabilityItem>() {

                    @Override
                    public int compare(
                            CapabilitySearchResponse.CapabilityItem o1,
                            CapabilitySearchResponse.CapabilityItem o2) {

                        return Double.compare(
                                o2.getScore(),
                                o1.getScore()
                        );
                    }
                }
        );

        if (items.size() > limit) {

            items =
                    new ArrayList<
                            CapabilitySearchResponse.CapabilityItem>(
                            items.subList(0, limit)
                    );
        }

        CapabilitySearchResponse response =
                new CapabilitySearchResponse();

        response.setItems(items);

        return response;
    }


    /**
     * V1 简单匹配。
     *
     * 后期这里替换成：
     *
     * embedding
     * +
     * vector search
     * +
     * rerank
     */
    private double calculateScore(
            String query,
            CapabilityDO capability) {

        double score = 0D;

        /*
         * 直接搜 capabilityCode。
         */
        if (StringUtils.hasText(
                capability.getCapabilityCode())
                &&
                query.contains(
                        capability.getCapabilityCode())) {

            return 1D;
        }

        /*
         * 能力名称。
         */
        if (StringUtils.hasText(
                capability.getCapabilityName())) {

            String name =
                    capability.getCapabilityName();

            if (query.contains(name)) {
                score = Math.max(score, 0.95D);
            }
        }

        /*
         * keywords:
         *
         * 天气,温度,天气预报,下雨
         */
        if (StringUtils.hasText(
                capability.getKeywords())) {

            String[] keywords =
                    capability
                            .getKeywords()
                            .split(",");

            int matchCount = 0;

            for (String keyword : keywords) {

                if (!StringUtils.hasText(keyword)) {
                    continue;
                }

                String value = keyword.trim();

                if (query.contains(value)) {
                    matchCount++;
                }
            }

            if (matchCount > 0) {

                score = Math.max(
                        score,
                        Math.min(
                                0.99D,
                                0.90D
                                        +
                                        (matchCount - 1)
                                                * 0.02D
                        )
                );
            }
        }

        /*
         * description 这里只做非常简单判断。
         */
        if (score == 0D
                &&
                StringUtils.hasText(
                        capability.getDescription())) {

            String description =
                    capability.getDescription();

            if (description.contains(query)) {
                score = 0.80D;
            }
        }

        return score;
    }

}
