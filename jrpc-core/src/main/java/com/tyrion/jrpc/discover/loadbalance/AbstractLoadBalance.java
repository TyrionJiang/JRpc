package com.tyrion.jrpc.discover.loadbalance;

import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author TyrionJ
 * @date 2020/7/13 11:30
 */
public abstract class AbstractLoadBalance implements ILoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceAddresses) {
        return StringUtils.isEmpty(serviceAddresses) ? null : doSelect(serviceAddresses);
    }

    protected abstract String doSelect(List<String> serviceAddresses);
}
