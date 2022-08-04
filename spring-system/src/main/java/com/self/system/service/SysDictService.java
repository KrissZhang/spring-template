package com.self.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.self.common.constants.CacheConstants;
import com.self.common.utils.RedisUtils;
import com.self.dao.entity.SysDictData;
import com.self.dao.service.SysDictDataService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SysDictService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SysDictDataService sysDictDataService;

    public List<SysDictData> getDictList(String dictTypeCode){
        if(StringUtils.isBlank(dictTypeCode)){
            return Lists.newArrayListWithCapacity(0);
        }

        List<SysDictData> dictList = redisUtils.get(CacheConstants.SYS_DICT_KEY + dictTypeCode);

        if(!CollectionUtils.isEmpty(dictList)){
            return dictList;
        }

        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getStatus, NumberUtils.BYTE_ONE).eq(SysDictData::getDictTypeCode, dictTypeCode);
        wrapper.orderByAsc(SysDictData::getDictSort, SysDictData::getId);
        dictList = sysDictDataService.list(wrapper);

        redisUtils.set(CacheConstants.SYS_DICT_KEY + dictTypeCode, dictList);

        return dictList;
    }

}
