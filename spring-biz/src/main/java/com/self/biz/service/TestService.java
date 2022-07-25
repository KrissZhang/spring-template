package com.self.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableMap;
import com.self.common.api.condition.test.TestListCondition;
import com.self.common.api.req.test.TestListReq;
import com.self.common.api.resp.test.TestListResp;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.EnableEnum;
import com.self.common.utils.BeanUtils;
import com.self.dao.entity.Test;
import com.self.dao.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestService {

    @Autowired
    private TestMapper testMapper;

    public ResultEntity<String> testReq(String req){
        return ResultEntity.ok("testKey:" + req);
    }

    public ResultEntity<Page<TestListResp>> testPage(TestListReq testListReq){
        Page<TestListResp> page = new Page<>(testListReq.getCurrentPage(), testListReq.getPageSize());

        TestListCondition condition = BeanUtils.copyProperties(testListReq, TestListCondition.class);
        List<Test> testList = testMapper.selectAllByNameTestList(page, condition);

        List<TestListResp> respList = BeanUtils.copyList(testList, TestListResp.class);

        ImmutableMap<Byte, EnableEnum> enableMap = EnableEnum.enableMap();
        respList.forEach(r -> Optional.ofNullable(enableMap.get(r.getEnable())).ifPresent(enableEnum -> r.setEnableName(enableEnum.getDesc())));

        page.setRecords(respList);

        return ResultEntity.ok(page);
    }

}
