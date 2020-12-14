package com.tyrion.jrpc.service;

import com.tyrion.jrpc.annotation.RpcService;
import com.tyrion.jrpc.api.dto.User;
import com.tyrion.jrpc.api.service.UserService;

/**
 * @author TyrionJ
 * @date 2020/7/13 18:45
 */
@RpcService(version = "1.0")
public class UserService2Impl implements UserService {

    @Override
    public User findById(Integer userId) {
        User user = new User();
        user.setId(userId);
        user.setName("Lily");
        return user;
    }
}
