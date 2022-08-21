package com.wjx.community.service;

import java.util.Map;

/**
 * @author wjx
 * @description
 */
public interface LoginService {
    Map<String, Object> login(String username, String password, long expireTime);

    Map<String, Object> getCode(String email);

    Map<String, Object> resetPwd(String email, String code,String password);
}
