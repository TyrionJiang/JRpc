package com.tyrion.jrpc.api.service;

import com.tyrion.jrpc.api.dto.User;

public interface UserService {

    User findById(Integer userId);
}
