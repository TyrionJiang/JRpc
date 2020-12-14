package com.tyrion.jrpc.processor;

import com.tyrion.jrpc.annotation.RpcReference;
import com.tyrion.jrpc.client.RpcClient;
import com.tyrion.jrpc.client.RpcInvocationHandler;
import com.tyrion.jrpc.discover.IServerDiscover;
import com.tyrion.jrpc.discover.ZkServerDiscover;
import com.tyrion.jrpc.register.IregisterCenter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * 对{@link RpcReference}修饰的属性注入服务代理
 *
 * @author TyrionJ
 * @date 2020/7/13 11:31
 */
@Component
public class RpcBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private IregisterCenter zkRegisterCenter;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] fields = null == beanClass ? null : beanClass.getDeclaredFields();
        if (null == fields) {
            return bean;
        }

        IServerDiscover serverDiscover = null;
        for (Field field : fields) {
            RpcReference annotation = field.getAnnotation(RpcReference.class);
            if (null == annotation) {
                continue;
            }

            if (null == serverDiscover) {
                serverDiscover = new ZkServerDiscover(zkRegisterCenter);
            }

            try {
                field.setAccessible(true);
                // 将服务代理后注入
                field.set(bean, Proxy.newProxyInstance(field.getType().getClassLoader(), new Class[]{field.getType()},
                        new RpcInvocationHandler(serverDiscover, new RpcClient(), annotation.version())));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return bean;
    }
}