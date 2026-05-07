package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识需要自动填充的字段（insert、update）
 */
@Target(ElementType.METHOD) // 方法级别
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
public @interface AutoFill {
    //枚举 类型
    OperationType value();

}
