package com.self.common.utils;

import com.google.common.collect.Lists;
import com.self.common.domain.TreeGrid;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 树
 */
public class TreeUtils {

    /**
     * 列表转树形列表
     */
    public static <T extends TreeGrid> List<T> formatTree(List<T> list){
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayListWithCapacity(0);
        }

        Map<Long, List<T>> listMap = list.stream().collect(Collectors.groupingBy(T::getParentId));

        list.forEach(grid -> grid.setChildren(listMap.get(grid.getId()) == null ? Lists.newArrayListWithCapacity(0) : listMap.get(grid.getId())));

        return list.stream().filter(item -> NumberUtils.LONG_ZERO.equals(item.getParentId()))
                .collect(Collectors.toList());
    }

}
