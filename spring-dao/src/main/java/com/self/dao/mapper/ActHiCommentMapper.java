package com.self.dao.mapper;

import com.self.dao.entity.ActHiComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.self.dao.entity.ActHiComment
 */
public interface ActHiCommentMapper extends BaseMapper<ActHiComment> {

    List<ActHiComment> selectBatchByProcessInstanceIds(@Param("processInstanceIds") List<String> processInstanceIds);

}




