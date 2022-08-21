package com.wjx.community.service;

import com.wjx.community.entity.User;

import java.util.Map;

/**
 * @author wjx
 * @description
 */
public interface UserService {
    Map<String, Object> register(User user);

    int activation(Integer userId, String code);

    User getOneUser(String userId);

    User getUserByName(String toName);
}
