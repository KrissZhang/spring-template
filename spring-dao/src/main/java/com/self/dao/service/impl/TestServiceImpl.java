package com.self.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.self.dao.entity.Test;
import com.self.dao.service.TestService;
import com.self.dao.mapper.TestMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test>
    implements TestService{

}




