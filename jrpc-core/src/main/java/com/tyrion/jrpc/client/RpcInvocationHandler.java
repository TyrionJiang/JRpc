package com.tyrion.jrpc.client;

import com.alibaba.fastjson.JSONObject;
import com.tyrion.jrpc.common.ResponseStatus;
import com.tyrion.jrpc.common.RpcRequest;
import com.tyrion.jrpc.common.RpcResponse;
import com.tyrion.jrpc.discover.IServerDiscover;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author TyrionJ
 * @date 2020/7/13 11:31
 */
@Slf4j
@AllArgsConstructor
public class RpcInvocationHandler implements InvocationHandler {

    private IServerDiscover serverDiscover;

    private RpcClient rpcClient;

    private String version;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParams(args);
        request.setVersion(version);

        String serviceName = method.getDeclaringClass().getName();
        String servicePath = serverDiscover.discover(StringUtils.isEmpty(version) ?
                serviceName : serviceName + "-" + version);
        if (null == servicePath) {
            log.error("No provider available for the service:{}, service version:{}", serviceName, version);
            throw new IllegalStateException("No provider available for the service:" + serviceName + ", service version:" + version);
        }
        String host = servicePath.split(":")[0];
        int port = Integer.parseInt(servicePath.split(":")[1]);

        RpcResponse response = rpcClient.host(host).port(port).send(request);
        if (null == response) {
            throw new RuntimeException("调用服务失败, servicePath:" + servicePath);
        }
        if (null == response.getCode() || response.getCode() != ResponseStatus.HTTP_OK) {
            log.error("调用服务失败, servicePath:{}, RpcResponse:{}", servicePath, JSONObject.toJSONString(response));
            throw new RuntimeException(response.getMessage());
        } else {
            return JSONObject.parseObject(JSONObject.toJSONString(response.getData()), method.getReturnType());
        }
    }
}
