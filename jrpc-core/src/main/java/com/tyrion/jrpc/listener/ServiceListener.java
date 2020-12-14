package com.tyrion.jrpc.listener;

import com.tyrion.jrpc.annotation.RpcService;
import com.tyrion.jrpc.register.IregisterCenter;
import com.tyrion.jrpc.server.RpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 程序启动，将{@link RpcService}修饰的服务完成注册
 *
 * @author TyrionJ
 * @date 2020/7/13 11:31
 */
@Component
public class ServiceListener implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private IregisterCenter zkRegisterCenter;

    @Value("${info.port}")
    private int port;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        ConfigurableApplicationContext context = applicationStartedEvent.getApplicationContext();
        try {
            RpcServer rpcServer = new RpcServer(zkRegisterCenter, InetAddress.getLocalHost().getHostAddress(), port);
            List<Object> services = new ArrayList<>(context.getBeansWithAnnotation(RpcService.class).values());
            rpcServer.register(services);
            rpcServer.expose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}