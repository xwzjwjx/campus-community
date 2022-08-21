package com.wjx.community.service.Impl;

import com.wjx.community.common.UserLocal;
import com.wjx.community.dao.UserMapper;
import com.wjx.community.entity.User;
import com.wjx.community.service.LoginService;
import com.wjx.community.util.EncryptionUtils;
import com.wjx.community.util.MailClient;
import com.wjx.community.util.RedisKeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author wjx
 * @description
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private MailClient mailClient;
    @Override
    public Map<String, Object> login(String username, String password, long expireTime) {
        Map<String, Object> map = new HashMap<>(8);

        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        User user = userMapper.selectUserByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活");
            return map;
        }
        password = EncryptionUtils.createMD5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误");
            return map;
        }
        String redisValue = RedisKeyUtils.getToken(user.getId());
        String redisKey = RedisKeyUtils.getLoginKey(user.getId());
        redisTemplate.opsForValue().set(redisKey,redisValue,expireTime, TimeUnit.SECONDS);
        Long expire = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        if (expire>0){
            UserLocal.clear();
            map.put("ticket",redisKey);
            UserLocal.set(user);
        }
        return map;
    }

    /**
     * 获取邮箱验证码
     * @param email
     * @return
     */
    @Override
    public Map<String, Object> getCode(String email) {
        Map<String, Object> map = new HashMap<>(8);
        if (email == null || StringUtils.isBlank(email)){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        User user = userMapper.selectUserByEmail(email);
        if (user == null){
            map.put("emailMsg","该邮箱未绑定账号");
            return map;
        }
        //生成验证码
        String code = EncryptionUtils.createUUID().substring(0, 6);
        Context context = new Context();
        context.setVariable("email",email);
        context.setVariable("code",code);
        String process = templateEngine.process("/mail/forget", context);
        new Thread(()->{
            mailClient.sendMail(email,"忘记密码？",process);
        }).start();
        String redisKey = RedisKeyUtils.getEmailKey(email);
        redisTemplate.opsForValue().set(redisKey,code,300,TimeUnit.SECONDS);
        Long expire = redisTemplate.getExpire(redisKey);
        if (expire>0){
            map.put("code",code);

        }
        return map;
    }

    /**
     * 密码重置
     * @param email
     * @param code
     * @param password
     * @return
     */
    @Override
    public Map<String, Object> resetPwd(String email, String code,String password) {
        Map<String, Object> map = new HashMap<>(8);
        if (password == null || StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        String redisKey = RedisKeyUtils.getEmailKey(email);
        Long expire = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        if (expire <= 0 ){
            map.put("codeMsg","验证码失效");
            return map;
        }
        String code1 = (String) redisTemplate.opsForValue().get(redisKey);
        if (code1 == null || StringUtils.isBlank(code)){
            map.put("emailMsg","邮箱有误");
            return map;
        }
        if (code1.equals(code)){
            map.put("codeMsg","验证码填写错误");
            return map;
        }
        Integer res = userMapper.resetPwd(email,password);
        if (res.equals(1)){
            map.put("res",true);
            redisTemplate.delete(redisKey);
        }else {
            map.put("res",false);
        }
        return map;
    }
}
