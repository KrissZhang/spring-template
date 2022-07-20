package com.self.security.utils;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BeanUtils {

    public static <T> T copyProperties(Object source, Class<T> tClass) {
        try {
            if (source == null) {
                return null;
            }

            T newInstance = tClass.newInstance();
            org.springframework.beans.BeanUtils.copyProperties(source, newInstance);

            return newInstance;
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, Object> beanToMapNotNull(Object o){
        Map<String, Object> result = new HashMap<>();
        Class<?> tClass = o.getClass();
        Field[] fields = tClass.getDeclaredFields();
        for(Field f : fields){
            f.setAccessible(true);
            try {
                String name = f.getName();
                String key = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
                Object o1 = f.get(o);
                if(o1 == null){
                    continue;
                }else if(o1 instanceof String && StringUtils.isEmpty((String)o1)){
                    continue;
                }else{
                    result.put(key, o1);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static <E, T> List<E> copyList(List<T> srcList, Class<E> clazz) {
        if (CollectionUtils.isEmpty(srcList)) {
            return Collections.emptyList();
        }

        return srcList.stream().map(src -> copyProperties(src, clazz)).collect(Collectors.toList());
    }

}
