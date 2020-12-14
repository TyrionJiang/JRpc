package com.tyrion.jrpc.annotation;

import java.lang.annotation.*;

/**
 * @author TyrionJ
 * @date 2020/7/13 11:31
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcReference {

    /**
     * service version
     */
    String version() default "";
}
