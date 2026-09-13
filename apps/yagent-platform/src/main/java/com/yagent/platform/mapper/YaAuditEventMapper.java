package com.yagent.platform.mapper;

import com.yagent.platform.domain.YaAuditEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface YaAuditEventMapper {
    int insert(YaAuditEvent event);
}
