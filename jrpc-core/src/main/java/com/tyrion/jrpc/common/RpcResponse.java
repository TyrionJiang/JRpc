package com.tyrion.jrpc.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Rpc服务响应实体类
 *
 * @author TyrionJ
 * @date 2020/7/14 11:33
 */
@Getter
@Setter
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 715745410605631233L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应错误消息体
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应
     *
     * @param data 数据
     * @param <T>  数据泛型
     * @return RpcResponse
     */
    public static <T> RpcResponse<T> success(T data) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(ResponseStatus.HTTP_OK);
        if (null != data) {
            response.setData(data);
        }
        return response;
    }

    /**
     * 失败响应
     *
     * @param status       响应码
     * @param errorMessage 错误消息
     * @param <T>          泛型
     * @return RpcResponse
     */
    public static <T> RpcResponse<T> fail(int status, String errorMessage) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(status);
        response.setMessage(errorMessage);
        return response;
    }
}
