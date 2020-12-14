package com.tyrion.jrpc.register;

import java.util.List;

/**
 * 注册中心接口
 *
 * @author TyrionJ
 * @date 2020/7/13 11:31
 */
public interface IregisterCenter {

    /**
     * 注册服务
     *
     * @param serviceName    服务名称
     * @param serviceAddress 服务地址
     * @throws Exception 节点创建失败
     */
    void register(String serviceName, String serviceAddress) throws Exception;

    /**
     * 监听服务节点变化
     *
     * @param serviceName 服务名称
     */
    void monitor(String serviceName);

    /**
     * 获取所有服务节点
     *
     * @param serviceName 服务名称
     * @return java.util.List<java.lang.String>
     */
    List<String> getNodes(String serviceName);
}
