package com.self.web.controller;

import com.self.common.constants.ApiURI;
import com.self.common.domain.ResultEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping(value = ApiURI.TEST_ONE)
    public ResultEntity<String> test1(@RequestParam String req){
        return ResultEntity.ok("testKey:" + req);
    }

}
