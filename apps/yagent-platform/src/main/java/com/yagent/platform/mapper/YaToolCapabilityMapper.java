package com.yagent.platform.mapper;
import com.yagent.platform.domain.YaToolCapability;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper public interface YaToolCapabilityMapper {
    List<YaToolCapability> selectByCapabilityCodes(@Param("codes") List<String> codes);
    List<YaToolCapability> selectByCapabilityCode(@Param("code") String code);
}
