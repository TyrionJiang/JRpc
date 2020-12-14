package com.tyrion.jrpc.web;

import com.tyrion.jrpc.annotation.RpcReference;
import com.tyrion.jrpc.api.dto.User;
import com.tyrion.jrpc.api.service.UserService;
import com.tyrion.jrpc.common.RpcResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TyrionJ
 * @date 2020/7/14 20:00
 */
@RestController
@RequestMapping("/user2")
public class User2Controller {

    @RpcReference(version = "1.0")
    private UserService userService;

    @GetMapping("/{id}")
    public RpcResponse<User> findById(@PathVariable Integer id) {
        User user = userService.findById(id);
        return RpcResponse.success(user);
    }
}