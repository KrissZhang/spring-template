package com.self.common.annotation;

import com.self.common.enums.BusinessTypeEnum;
import com.self.common.enums.OperatorTypeEnum;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    /**
     * 模块
     */
    String title() default "";

    /**
     * 功能类型
     */
    BusinessTypeEnum businessType() default BusinessTypeEnum.OTHER;

    /**
     * 操作人类别
     */
    OperatorTypeEnum operatorType() default OperatorTypeEnum.OTHER;

    /**
     * 是否保存请求的参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    boolean isSaveResponseData() default true;

}
