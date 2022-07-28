package com.self.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.self.common.api.condition.test.TestListCondition;
import com.self.common.api.req.test.TestAddReq;
import com.self.common.api.req.test.TestListReq;
import com.self.common.api.resp.test.TestListResp;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.EnableEnum;
import com.self.common.exception.BizException;
import com.self.common.utils.BeanUtils;
import com.self.common.utils.TransactionUtils;
import com.self.dao.api.page.PagingResp;
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

    public ResultEntity<PagingResp<TestListResp>> testPage(TestListReq testListReq){
        Page<TestListResp> page = Page.of(testListReq.getCurrentPage(), testListReq.getPageSize());

        TestListCondition condition = BeanUtils.copyProperties(testListReq, TestListCondition.class);
        List<Test> testList = testMapper.selectAllByNameTestList(page, condition);

        List<TestListResp> respList = BeanUtils.copyList(testList, TestListResp.class);

        respList.forEach(r -> Optional.ofNullable(EnableEnum.resolve(r.getEnable())).ifPresent(enableEnum -> r.setEnableName(enableEnum.getDesc())));

        return ResultEntity.ok(new PagingResp<>(page, respList));
    }

    public ResultEntity<Object> testTransaction(TestAddReq testAddReq){
        Test test = BeanUtils.copyProperties(testAddReq, Test.class);
        test.setEnable(EnableEnum.ENABLE.getValue());

        TransactionUtils.beginTransaction(() -> {
            testMapper.insert(test);

            /**
             * 事务内部异常会回滚
            if(test.getId() % 2 != 0){
                throw new BizException("测试异常");
            }*/
        });

        //事务外部异常不会回滚
        if(test.getId() % 2 != 0){
            throw new BizException("测试异常");
        }

        return ResultEntity.ok();
    }

}
