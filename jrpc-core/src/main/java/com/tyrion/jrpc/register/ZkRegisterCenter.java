package com.tyrion.jrpc.register;

import com.tyrion.jrpc.discover.ZkServerDiscover;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

/**
 * @author TyrionJ
 * @date 2020/7/13 11:31
 */
@Slf4j
@Component
public class ZkRegisterCenter implements IregisterCenter {

    @Value("${info.zkAddress}")
    private String zkAddress;

    private CuratorFramework curatorFramework;

    private static final String ZK_REGISTER_PATH = "/jrpc";

    @PostConstruct
    public void start() {
        log.info(">>> Starting zookeeper");
        this.curatorFramework = CuratorFrameworkFactory.builder().connectString(zkAddress).sessionTimeoutMs(15000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10)).build();
        this.curatorFramework.start();
    }

    @PreDestroy
    public void close() {
        log.info("<<< Shutting down zookeeper");
        this.curatorFramework.close();
    }

    @Override
    public List<String> getNodes(String serviceName) {
        List<String> nodeList = null;
        try {
            nodeList = curatorFramework.getChildren().forPath(ZK_REGISTER_PATH + "/" + serviceName);
        } catch (Exception e) {
            if (e instanceof KeeperException.NoNodeException) {
                log.error("未获得该节点, serviceName:{}", serviceName);
            } else {
                throw new RuntimeException("获取子节点异常：" + e);
            }
        }
        return nodeList;
    }

    @Override
    public void register(String serviceName, String serviceAddress) throws Exception {
        // 需要注册的服务根节点
        String servicePath = ZK_REGISTER_PATH + "/" + serviceName;
        // 注册服务，创建临时节点
        String serviceAddr = servicePath + "/" + serviceAddress;
        String nodePath = this.curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .forPath(serviceAddr, "".getBytes());
        log.info("节点创建成功, 节点为:{}", nodePath);
    }

    @Override
    public void monitor(String serviceName) {
        String path = ZkRegisterCenter.ZK_REGISTER_PATH + "/" + serviceName;
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, path, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(path);
            ZkServerDiscover.SERVICE_NODE_CACHE.put(serviceName, serviceAddresses);
        };
        childrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            childrenCache.start();
        } catch (Exception e) {
            throw new RuntimeException("注册PatchChild Watcher 异常" + e);
        }
    }
}
