package com.self.web.controller;

import com.self.biz.service.TestService;
import com.self.common.annotation.OperLog;
import com.self.common.api.req.test.TestAddReq;
import com.self.common.api.req.test.TestListReq;
import com.self.common.api.resp.test.TestListResp;
import com.self.common.constants.ApiURI;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.BusinessTypeEnum;
import com.self.dao.api.page.PagingResp;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "测试管理")
@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @Operation(summary = "测试请求")
    @OperLog(title = "测试请求", businessType = BusinessTypeEnum.OTHER)
    @GetMapping(value = ApiURI.TEST_REQ)
    public ResultEntity<String> testReq(@Parameter(description = "请求参数") @RequestParam String req){
        return testService.testReq(req);
    }

    @Operation(summary = "测试分页")
    @OperLog(title = "测试分页", businessType = BusinessTypeEnum.OTHER)
    @PostMapping(value = ApiURI.TEST_PAGE)
    public ResultEntity<PagingResp<TestListResp>> testPage(@RequestBody @Validated TestListReq testListReq){
        return testService.testPage(testListReq);
    }

    @Operation(summary = "测试事务")
    @OperLog(title = "测试事务", businessType = BusinessTypeEnum.ADD)
    @PostMapping(value = ApiURI.TEST_TRANSACTION)
    public ResultEntity<Object> testTransaction(@RequestBody @Validated TestAddReq testAddReq){
        return testService.testTransaction(testAddReq);
    }

}
