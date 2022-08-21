package com.wjx.community.common;

import com.wjx.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author wjx
 * @description
 */
public class UserLocal {
    private UserLocal(){}
    private static final ThreadLocal<User> USER_LOCAL = new ThreadLocal<>();
    public static void set(User user){
        USER_LOCAL.set(user);
    }
    public static User get(){
        return USER_LOCAL.get();
    }

    public static void clear(){
        USER_LOCAL.remove();
    }

}
