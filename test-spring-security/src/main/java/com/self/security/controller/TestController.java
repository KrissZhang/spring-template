package com.self.security.controller;

import com.self.security.constants.ApiURI;
import com.self.security.domain.ResultEntity;
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
