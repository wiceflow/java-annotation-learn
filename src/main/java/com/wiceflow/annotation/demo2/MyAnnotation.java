package com.wiceflow.annotation.demo2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author BF
 * @date 2018/3/5
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {

    String[] value1() default "abc";
}
