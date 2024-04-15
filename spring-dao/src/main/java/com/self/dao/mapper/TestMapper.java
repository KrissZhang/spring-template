package com.self.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.self.common.api.condition.test.TestListCondition;
import com.self.dao.entity.Test;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.self.dao.entity.Test
 */
public interface TestMapper extends BaseMapper<Test> {

    List<Test> selectAllByNameTestList(@Param("aesKey") String aesKey, @Param("condition") TestListCondition condition);

    int insertBatch(@Param("lists") List<Test> lists);

}




