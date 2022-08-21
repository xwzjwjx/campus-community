package com.wjx.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wjx
 * @description
 */
public class CookieUtil {

    /**
     * 取出cookie中的值
     * @param request
     * @param name
     * @return
     */
    public static String getValue(HttpServletRequest request,String name){
        if (request == null || name == null){
            new RuntimeException("参数不能为空！");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
