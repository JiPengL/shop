package com.ixuxie.annotation;


import java.lang.annotation.*;

/**
 * @author wuhui
 * @date 2018/8/21 14:17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Auth {

    boolean login() default true;
}
