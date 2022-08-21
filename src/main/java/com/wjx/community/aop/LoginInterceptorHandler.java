package com.wjx.community.aop;

import com.wjx.community.common.UserLocal;
import com.wjx.community.dao.UserMapper;
import com.wjx.community.entity.User;
import com.wjx.community.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wjx
 * @description 登录拦截器
 */
@Component
public class LoginInterceptorHandler implements HandlerInterceptor {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserMapper userMapper;
    /**
     * 在请求到达Controller控制器之前 通过拦截器执行一段代码
     * 如果方法返回true,继续执行后续操作
     * 如果返回false，执行中断请求处理，请求不会发送到Controller
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null){
            String value = (String) redisTemplate.opsForValue().get(ticket);
            if (value != null && StringUtils.isNotBlank(value)){
                int i = value.lastIndexOf(':');
                Integer userId = Integer.valueOf(value.substring(i+1));
                User user = userMapper.selectById(userId);
                if (user != null){
                    UserLocal.set(user);

                }
            }
        }
        return true;

    }

    /**
     * 控制器之后，跳转前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView 可进行参数传递和路径跳转
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        User user = UserLocal.get();
        if (user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserLocal.clear();
    }
}
