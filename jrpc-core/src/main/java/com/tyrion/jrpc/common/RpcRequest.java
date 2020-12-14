package com.tyrion.jrpc.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * Rpc接口请求实体类
 *
 * @author TyrionJ
 * @date 2020/7/14 11:33
 */
@Getter
@Setter
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 5661720043123218215L;

    /**
     * 请求接口名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数数组
     */
    private Object[] params;

    /**
     * 版本号
     */
    private String version;
}
