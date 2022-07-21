package com.self.web.controller;

import com.self.common.constants.ApiURI;
import com.self.common.domain.ResultEntity;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "测试管理")
@RestController
public class TestController {

    @Operation(summary = "测试请求")
    @GetMapping(value = ApiURI.TEST_ONE)
    public ResultEntity<String> test1(@Parameter(description = "请求参数") @RequestParam String req){
        return ResultEntity.ok("testKey:" + req);
    }

}
