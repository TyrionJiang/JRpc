package com.tyrion.jrpc.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author TyrionJ
 * @date 2020/7/13 11:31
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface RpcService {

    /**
     * service version
     */
    String version() default "";
}
