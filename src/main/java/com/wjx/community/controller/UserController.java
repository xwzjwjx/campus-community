package com.wjx.community.controller;

import com.wjx.community.aop.annotation.LoginRequired;
import com.wjx.community.common.UserLocal;
import com.wjx.community.entity.User;
import com.wjx.community.service.Impl.FollowService;
import com.wjx.community.service.Impl.LikeService;
import com.wjx.community.service.UserService;
import com.wjx.community.util.EncryptionUtils;
import com.wjx.community.util.ResponseUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.wjx.community.common.CommunityConstant.ENTITY_TYPE_USER;

/**
 * @author wjx
 * @description
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

//    @Value("${community.path.upload}")
//    private String uploadPath;
//
//    @Value("${community.path.domain}")
//    private String domain;
//
//    @Value("${server.servlet.context-path}")
//    private String contextPath;

    @Autowired
    private UserService userService;


    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

//    @Value("${qiniu.key.access}")
//    private String accessKey;
//
//    @Value("${qiniu.key.secret}")
//    private String secretKey;
//
//    @Value("${qiniu.bucket.header.name}")
//    private String headerBucketName;
//
//    @Value("${quniu.bucket.header.url}")
//    private String headerBucketUrl;

//    @LoginRequired
//    @RequestMapping(path = "/setting", method = RequestMethod.GET)
//    public String getSettingPage(Model model) {
//        // 上传文件名称
//        String fileName = EncryptionUtils.createUUID();
//        // 设置响应信息
//        StringMap policy = new StringMap();
//        policy.put("returnBody", CommunityUtil.getJSONString(0));
//        // 生成上传凭证
//        Auth auth = Auth.create(accessKey, secretKey);
//        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);
//
//        model.addAttribute("uploadToken", uploadToken);
//        model.addAttribute("fileName", fileName);
//
//        return "/site/setting";
//    }

//    // 更新头像路径
//    @RequestMapping(path = "/header/url", method = RequestMethod.POST)
//    @ResponseBody
//    public String updateHeaderUrl(String fileName) {
//        if (StringUtils.isBlank(fileName)) {
//            return ResponseUtils.getJSONString(1, "文件名不能为空!");
//        }
//
//        String url = headerBucketUrl + "/" + fileName;
//        userService.updateHeader(UserLocal.get().getId(), url);
//
//        return ResponseUtils.getJSONString(0);
//    }
//
//    // 废弃
//    @LoginRequired
//    @RequestMapping(path = "/upload", method = RequestMethod.POST)
//    public String uploadHeader(MultipartFile headerImage, Model model) {
//        if (headerImage == null) {
//            model.addAttribute("error", "您还没有选择图片!");
//            return "/site/setting";
//        }
//
//        String fileName = headerImage.getOriginalFilename();
//        String suffix = fileName.substring(fileName.lastIndexOf("."));
//        if (StringUtils.isBlank(suffix)) {
//            model.addAttribute("error", "文件的格式不正确!");
//            return "/site/setting";
//        }
//
//        // 生成随机文件名
//        fileName = EncryptionUtils.createUUID() + suffix;
//        // 确定文件存放的路径
//        File dest = new File(uploadPath + "/" + fileName);
//        try {
//            // 存储文件
//            headerImage.transferTo(dest);
//        } catch (IOException e) {
//            logger.error("上传文件失败: " + e.getMessage());
//            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
//        }
//
//        // 更新当前用户的头像的路径(web访问路径)
//        // http://localhost:8080/community/user/header/xxx.png
//        User user = UserLocal.get();
//        String headerUrl = domain + contextPath + "/user/header/" + fileName;
//        userService.updateHeader(user.getId(), headerUrl);
//
//        return "redirect:/index";
//    }
//
//    // 废弃
//    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
//    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
//        // 服务器存放路径
//        fileName = uploadPath + "/" + fileName;
//        // 文件后缀
//        String suffix = fileName.substring(fileName.lastIndexOf("."));
//        // 响应图片
//        response.setContentType("image/" + suffix);
//        try (
//                FileInputStream fis = new FileInputStream(fileName);
//                OutputStream os = response.getOutputStream();
//        ) {
//            byte[] buffer = new byte[1024];
//            int b = 0;
//            while ((b = fis.read(buffer)) != -1) {
//                os.write(buffer, 0, b);
//            }
//        } catch (IOException e) {
//            logger.error("读取头像失败: " + e.getMessage());
//        }
//    }

    /**
     * 个人主页
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.getOneUser(String.valueOf(userId));
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.getUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (UserLocal.get() != null) {
            hasFollowed = followService.hasFollowed(UserLocal.get().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }
}
