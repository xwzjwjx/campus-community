package com.wjx.community.controller;

import com.google.code.kaptcha.Producer;
import com.wjx.community.entity.User;
import com.wjx.community.service.LoginService;
import com.wjx.community.service.UserService;
import com.wjx.community.util.CookieUtil;
import com.wjx.community.util.EncryptionUtils;
import com.wjx.community.util.RedisKeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.wjx.community.common.CommunityConstant.*;

/**
 * @author wjx
 * @description 登录注册
 */
@Controller
public class LoginController {

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Resource
    private UserService userService;
    @Resource
    private Producer kaptchaProducer;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private LoginService loginService;

    @RequestMapping("/register")
    public String register(){
        return "site/register";
    }
    @GetMapping("/login")
    public String login(){
        return "site/login";
    }
    @GetMapping("/forget")
    public String forget(){
        return "site/forget";
    }

    @PostMapping("/register")
    public String register(Model model,User user){
        Map<String,Object> resultMap = userService.register(user);
        if (resultMap == null || resultMap.isEmpty()){
            model.addAttribute("msg", "注册成功！我们已经向您的邮箱发送了一份激活邮件，请在1分钟内激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg", resultMap.get("usernameMsg"));
            model.addAttribute("passwordMsg", resultMap.get("passwordMsg"));
            model.addAttribute("emailMsg", resultMap.get("emailMsg"));
            return "/site/register";
        }
    }

    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model,@PathVariable String code, @PathVariable Integer userId){
        int result = userService.activation(userId,code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功！您现在就可以使用账号登录了！");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "请勿重复激活！");
            model.addAttribute("target", "/index");
        } else if (result == ACTIVATION_TIMEOUT) {
            model.addAttribute("msg", "激活失败！验证超时，请重新注册账号！");
            model.addAttribute("target", "/index");
        }
        else {
            model.addAttribute("msg", "激活失败！请提供正确激活码！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpSession session, HttpServletResponse response){
        //生成图片验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        String kaptchaOwner = EncryptionUtils.createUUID();
        //添加到cookie
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setPath(contextPath);
        cookie.setMaxAge(60);
        response.addCookie(cookie);
        //将图片验证码存入Redis方便后续验证
        String redisKey = RedisKeyUtils.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);
        response.setContentType("image/png");
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            throw new RuntimeException("读取验证码图片失败，服务端异常");
        }
    }


    @PostMapping("/login")
    public String login(Model model,String username,String password,String code,
                        boolean rememberMe,HttpServletResponse response,
                        @CookieValue(value = "kaptchaOwner",required = false) String kaptchaOwner){
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)){
            kaptcha = (String) redisTemplate.opsForValue().get(RedisKeyUtils.getKaptchaKey(kaptchaOwner));
        }
        if (kaptcha == null || StringUtils.isBlank(kaptcha)){
            model.addAttribute("codeMsg","验证码已失效");
            return "site/login";
        }
        if (StringUtils.isBlank(code) || !code.equals(kaptcha)){
            model.addAttribute("codeMsg","验证码错误");
        }
        long expireTime = rememberMe? REMEMBER_EXPIRED_SECONDS: DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> resultMap = loginService.login(username,password,expireTime);
        if (resultMap.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",(String)resultMap.get("ticket"));
            cookie.setMaxAge(Math.toIntExact(expireTime));
            cookie.setPath(contextPath);
            response.addCookie(cookie);
            return "redirect:index";

        }else{
            model.addAttribute("usernameMsg",resultMap.get("usernameMsg"));
            model.addAttribute("passwordMsg",resultMap.get("passwordMsg"));
        }
        return "site/login";
    }

    @GetMapping("/code/{email}")
    public String getCode(@PathVariable String email,Model model){
        Map<String,Object> map = loginService.getCode(email);
        if (!map.containsKey("code")){
            model.addAttribute("emailMsg",map.get("emailMsg"));
        }
        return "site/forget";
    }

    @PostMapping("/forget")
    public String forget(String email,String code,String password,Model model){
        Map<String,Object> map = loginService.resetPwd(email,code,password);

        if (map.containsKey("passwordMsg")){
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
        }else if (map.containsKey("emailMsg")){
            model.addAttribute("emailMsg",map.get("emailMsg"));

        }else if (map.containsKey("codeMsg")){
            model.addAttribute("codeMsg",map.get("codeMsg"));
        }else {
            if ((boolean) map.get("res")){
                return "forward:logout";
            }else{
                model.addAttribute("emailMsg","邮箱不存在");
            }
        }
        return "site/forget";
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null || StringUtils.isNotBlank(ticket)){
            redisTemplate.delete(ticket);
        }
        return "redirect:index";
    }

}
