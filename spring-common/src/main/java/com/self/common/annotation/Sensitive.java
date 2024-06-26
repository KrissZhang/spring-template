package com.self.common.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.self.common.config.SensitiveSerialize;
import com.self.common.enums.SensitiveTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerialize.class)
public @interface Sensitive {

    /**
     * 脱敏数据类型
     */
    SensitiveTypeEnum type() default SensitiveTypeEnum.CUSTOM;

    /**
     * 前置不需要打码的长度
     */
    int prefixNoMaskLen() default 0;

    /**
     * 后置不需要打码的长度
     */
    int suffixNoMaskLen() default 0;

    /**
     * 打码字符
     */
    String symbol() default "*";

}
