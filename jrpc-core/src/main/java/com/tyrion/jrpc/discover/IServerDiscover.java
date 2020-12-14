package com.tyrion.jrpc.discover;

/**
 * 服务发现接口
 *
 * @author TyrionJ
 * @date 2020/7/13 11:31
 */
public interface IServerDiscover {

    /**
     * 基于服务名称获得一个远程地址
     *
     * @param serviceName 服务名称
     * @return 远程地址
     */
    String discover(String serviceName);
}
