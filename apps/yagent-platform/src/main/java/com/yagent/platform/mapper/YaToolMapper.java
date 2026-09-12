package com.yagent.platform.mapper;
import com.yagent.platform.domain.YaTool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper public interface YaToolMapper {
    List<YaTool> selectActiveByIds(@Param("toolIds") List<String> toolIds);
}
