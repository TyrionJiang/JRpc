package com.tyrion.jrpc.server;

import com.alibaba.fastjson.JSON;
import com.tyrion.jrpc.common.ResponseStatus;
import com.tyrion.jrpc.common.RpcRequest;
import com.tyrion.jrpc.common.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * 远程请求处理
 *
 * @author TyrionJ
 * @date 2020/7/14 11:38
 */
@Slf4j
public class ProcessRequestHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 服务映射
     */
    private Map<String, Object> serviceMap;

    public ProcessRequestHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String request) throws Exception {
        log.debug("receive request:{}", request);
        Object result = this.invoke(JSON.parseObject(request, RpcRequest.class));
        ChannelFuture future = channelHandlerContext.writeAndFlush(JSON.toJSONString(result));
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Unexpected exception from downstream.", cause);
        ctx.close();
    }

    /**
     * 服务调用返回处理结果
     *
     * @param request 服务请求
     * @return 处理结果
     */
    private Object invoke(RpcRequest request)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 获得服务名称
        String serviceName = request.getClassName();
        // 获得版本号
        String version = request.getVersion();
        // 获得方法名
        String methodName = request.getMethodName();
        // 获得参数数组
        Object[] params = request.getParams();
        // 获得参数类型数据
        Class<?>[] argTypes = Arrays.stream(params).map(Object::getClass).toArray(Class<?>[]::new);

        Object service = serviceMap.get(StringUtils.isEmpty(version) ? serviceName : serviceName + "-" + version);
        if (null == service) {
            return RpcResponse.fail(ResponseStatus.HTTP_NOT_FOUND, "未找到服务");
        }
        Method method = service.getClass().getMethod(methodName, argTypes);
        if (null == method) {
            return RpcResponse.fail(ResponseStatus.HTTP_NOT_FOUND, "未找到服务方法");
        }
        return RpcResponse.success(method.invoke(service, params));
    }
}
