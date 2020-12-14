package com.tyrion.jrpc.discover.loadbalance;

import java.util.List;

/**
 * @author TyrionJ
 * @date 2020/7/13 11:31
 */
public interface ILoadBalance {

    /**
     * 在已有服务列表中选择一个服务路径
     *
     * @param serviceAddresses 服务地址列表
     * @return 服务地址
     */
    String selectServiceAddress(List<String> serviceAddresses);
}
