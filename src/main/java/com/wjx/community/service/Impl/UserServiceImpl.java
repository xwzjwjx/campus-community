package com.wjx.community.service.Impl;

import com.wjx.community.dao.UserMapper;
import com.wjx.community.entity.User;
import com.wjx.community.service.UserService;
import com.wjx.community.util.EncryptionUtils;
import com.wjx.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.wjx.community.common.CommunityConstant.*;


/**
 * @author wjx
 * @description
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private MailClient mailClient;
    @Override
    public Map<String, Object> register(User userParam) {
        Map<String, Object> resultMap = new HashMap<>(8);
        //空值处理
        if (userParam == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        String email = userParam.getEmail();
        String username = userParam.getUsername();
        String password = userParam.getPassword();
        if (StringUtils.isBlank(username)){
            resultMap.put("usernameMsg","用户名不能为空！");
            return resultMap;
        }
        if (StringUtils.isBlank(password)){
            resultMap.put("passwordMsg","密码不能为空！");
            return resultMap;
        }
        if (StringUtils.isBlank(email)){
            resultMap.put("emailMsg","邮箱不能为空！");
            return resultMap;
        }
        //根据用户名获取用户
        User user1 = userMapper.selectUserByName(username);
        if (user1 != null){
            resultMap.put("usernameMsg","该用户名已被注册！");
            return resultMap;
        }
        //根据邮箱获得用户
        User user2 = userMapper.selectUserByEmail(email);
        if (user2 != null){
            resultMap.put("emailMsg","该邮箱已注册！");
            return resultMap;
        }
        //注册用户
        String salts = EncryptionUtils.createSalt();
        userParam.setSalt(salts);
        userParam.setPassword(EncryptionUtils.createMD5(password+salts));
        userParam.setType(0);
        userParam.setStatus(0);
        userParam.setActivationCode(EncryptionUtils.createUUID());
        userParam.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        //userParam.setCreateTime(new Date());
        Integer add = userMapper.insertUser(userParam);
        //邮箱激活
        Context context = new Context();
        context.setVariable("email",userParam.getEmail());
        //url:http:localhost:8888/community/activation/id/code
        String url = domain + contextPath + "/activation/" + userParam.getId() + "/" + userParam.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);

        //发送邮件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mailClient.sendMail(userParam.getEmail(), "账号激活", content);
            }
        });
        thread.start();
        //该线程负责若没有激活则删除对应的用户
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                User user3 = userMapper.selectById(userParam.getId());
                if (user3 !=null && user3.getStatus() != 1) {
                    userMapper.deleteById(user3.getId());
                    //清除Redis中的缓存
                    //clearCache(user3.getId());
                }
            }
        });
        thread1.start();
        return resultMap;
    }

    /**
     * 激活码验证
     * @param userId
     * @param code
     * @return
     */
    @Override
    public int activation(Integer userId, String code) {
        User user = userMapper.selectById(userId);
        if (user == null){
            return ACTIVATION_TIMEOUT;
        }
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }
        if (user.getActivationCode().equals(code)){
            Integer update = userMapper.updateStatus(userId,1);
            if (update == 1){
                return ACTIVATION_SUCCESS;
            }
        }
        return ACTIVATION_FAILURE;
    }

    @Override
    public User getOneUser(String userId) {
        if (StringUtils.isBlank(userId)){
            new IllegalArgumentException("用户id为空");
        }
        User user = userMapper.selectById(Integer.valueOf(userId));
        return user;
    }

    @Override
    public User getUserByName(String toName) {
        if (StringUtils.isBlank(toName)){
            throw new IllegalArgumentException("目标用户不能为空");
        }
        User user = userMapper.selectUserByName(toName);
        return user;
    }
}
