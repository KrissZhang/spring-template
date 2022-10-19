package com.self.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.self.common.api.condition.test.TestListCondition;
import com.self.dao.entity.Test;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.self.dao.entity.Test
 */
public interface TestMapper extends BaseMapper<Test> {

    List<Test> selectAllByNameTestList(Page<?> page, @Param("condition") TestListCondition condition);

}




