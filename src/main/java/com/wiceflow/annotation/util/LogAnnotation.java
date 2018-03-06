package com.wiceflow.annotation.util;

import java.lang.annotation.*;

/**
 * @author BF
 * @date 2018/3/5
 * 日志注解
 *          只能作用在方法上
 *          运行时反射注入  加载在JVM中
 *          保留JAVADOC注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {
    /**
     * 操作描述
     * @return
     */
    String value() default "";


}
