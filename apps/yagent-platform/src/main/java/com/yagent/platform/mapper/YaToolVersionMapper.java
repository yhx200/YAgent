package com.yagent.platform.mapper;
import com.yagent.platform.domain.YaToolVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper public interface YaToolVersionMapper {
    YaToolVersion selectPublished(@Param("toolId") String toolId,@Param("version") String version);
}
