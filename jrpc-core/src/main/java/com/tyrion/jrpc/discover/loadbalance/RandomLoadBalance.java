package com.tyrion.jrpc.discover.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * 随机模式负载均衡
 *
 * @author TyrionJ
 * @date 2020/7/14 11:33
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect(List<String> serviceAddresses) {
        return serviceAddresses.size() == 1 ? serviceAddresses.get(0) :
                serviceAddresses.get(new Random().nextInt(serviceAddresses.size()));
    }
}
