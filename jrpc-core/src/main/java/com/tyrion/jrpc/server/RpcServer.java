package com.tyrion.jrpc.server;

import com.tyrion.jrpc.annotation.RpcService;
import com.tyrion.jrpc.register.IregisterCenter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TyrionJ
 * @date 2020/7/13 11:31
 */
@Slf4j
public class RpcServer {

    /**
     * 注册中心
     */
    private IregisterCenter registerCenter;

    /**
     * 服务发布的ip地址
     */
    private String serviceIp;

    /**
     * 服务发布端口
     */
    private int servicePort;

    /**
     * 服务名称和服务对象的关系
     */
    private Map<String, Object> serviceMap = new HashMap<>();

    public RpcServer(IregisterCenter iregisterCenter, String ip, int servicePort) {
        this.registerCenter = iregisterCenter;
        this.serviceIp = ip;
        this.servicePort = servicePort;
    }

    /**
     * 注册服务
     *
     * @param services 服务列表
     */
    public void register(List<Object> services) {
        services.forEach(service -> {
            String serviceName = service.getClass().getInterfaces()[0].getName();
            String version = service.getClass().getAnnotation(RpcService.class).version();
            String key = StringUtils.isEmpty(version) ? serviceName : serviceName + "-" + version;
            serviceMap.put(key, service);

            try {
                registerCenter.register(key, serviceIp + ":" + servicePort);
            } catch (Exception e) {
                log.error("服务注册失败, errorMsg:{}", e.getMessage());
                throw new RuntimeException("服务注册失败");
            }
            log.info("成功注册服务, 服务名称：{}, 服务地址：{}", key, serviceIp + ":" + servicePort);
        });
    }

    /**
     * 暴露服务
     */
    public void expose() throws InterruptedException {
        if (serviceMap.isEmpty()) {
            log.info("未检索到任何服务.");
            return;
        }

        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 数据分包、组包、粘包
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        ch.pipeline().addLast(new LengthFieldPrepender(4));
                        ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                        ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                        ch.pipeline().addLast(new ProcessRequestHandler(serviceMap));
                    }
                });

        bootstrap.bind(serviceIp, servicePort).sync();
        log.info("成功暴露服务, host:{}, port:{}", serviceIp, servicePort);
    }
}
