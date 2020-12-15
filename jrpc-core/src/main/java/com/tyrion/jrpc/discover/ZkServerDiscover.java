package com.tyrion.jrpc.discover;

import com.tyrion.jrpc.discover.loadbalance.ILoadBalance;
import com.tyrion.jrpc.discover.loadbalance.RandomLoadBalance;
import com.tyrion.jrpc.register.IregisterCenter;
import com.tyrion.jrpc.util.RpcThreadExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TyrionJ
 * @date 2020/7/13 11:31
 */
@Slf4j
public class ZkServerDiscover implements IServerDiscover {

    private ILoadBalance iLoadBalance;

    private IregisterCenter zkRegisterCenter;

    public static final Map<String, List<String>> SERVICE_NODE_CACHE = new ConcurrentHashMap<>();

    public ZkServerDiscover(IregisterCenter zkRegisterCenter) {
        // 负载均衡默认随机算法
        this(zkRegisterCenter, new RandomLoadBalance());
    }

    public ZkServerDiscover(IregisterCenter zkRegisterCenter, ILoadBalance iLoadBalance) {
        this.zkRegisterCenter = zkRegisterCenter;
        this.iLoadBalance = iLoadBalance;
    }

    @Override
    public String discover(String serviceName) {
        List<String> serviceAddresses;
        if (!SERVICE_NODE_CACHE.containsKey(serviceName)) {
            serviceAddresses = zkRegisterCenter.getNodes(serviceName);
            if (!CollectionUtils.isEmpty(serviceAddresses)) {
                SERVICE_NODE_CACHE.put(serviceName, serviceAddresses);
            }
            RpcThreadExecutor.execute(() -> zkRegisterCenter.monitor(serviceName));
        } else {
            serviceAddresses = SERVICE_NODE_CACHE.get(serviceName);
        }
        return iLoadBalance.selectServiceAddress(serviceAddresses);
    }
}
