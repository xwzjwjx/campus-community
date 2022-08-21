package com.wjx.community.controller;

import com.wjx.community.common.Page;
import com.wjx.community.common.UserLocal;
import com.wjx.community.entity.Comment;
import com.wjx.community.entity.DiscussPost;
import com.wjx.community.entity.User;
import com.wjx.community.service.CommentService;
import com.wjx.community.service.DiscussPostService;
import com.wjx.community.service.Impl.LikeService;
import com.wjx.community.service.UserService;
import com.wjx.community.util.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wjx.community.common.CommunityConstant.ENTITY_TYPE_COMMENT;
import static com.wjx.community.common.CommunityConstant.ENTITY_TYPE_POST;

/**
 * @author wjx
 * @description
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private UserService userService;

    @Resource
    private LikeService likeService;

    @Resource
    private CommentService commentService;

    @PostMapping("/add")
    @ResponseBody
    public String add(String title,String content){
        User user = UserLocal.get();
        if (user == null){
            return ResponseUtils.getJSONString(-1,"你还没有登录");
        }
        if (StringUtils.isBlank(title)){
            return ResponseUtils.getJSONString(-1,"标题不能为空");
        }
        if (StringUtils.isBlank(content)){
            return ResponseUtils.getJSONString(-1,"内容不能为空");
        }
        boolean res = discussPostService.add(title,content,user);
        if (res){
            return ResponseUtils.getJSONString(0,"发布成功");
        }
        return ResponseUtils.getJSONString(-1,"服务端错误");
    }
    @GetMapping("/detail/{discussPostId}")
    public String detail(@PathVariable String discussPostId, Model model, Page page){
        DiscussPost discussPost = discussPostService.getOneDiscussPost(discussPostId);
        model.addAttribute("post",discussPost);
        //作者
        User user = userService.getOneUser(discussPost.getUserId());
        model.addAttribute("user",user);
        //该帖子所获赞
        long likeCount = likeService.getEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
        model.addAttribute("likeCount", likeCount);
        //该用户对该帖子的点赞状态
        int likeStatus = UserLocal.get() == null ? 0 : likeService.getEntityLikeStatus(UserLocal.get().getId(), ENTITY_TYPE_POST, discussPost.getId());
        model.addAttribute("likeStatus", likeStatus);
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());
        //cvo , 获取该帖子的所有评论
        List<Comment> commentList = commentService.getAllCommnetByEntity(ENTITY_TYPE_POST,discussPost.getId(),page.getOffset(),page.getLimit());
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVO = new HashMap<>();
                commentVO.put("comment", comment);
                commentVO.put("user", userService.getOneUser(String.valueOf(comment.getUserId())));

                likeCount = likeService.getEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeCount", likeCount);
                likeStatus = UserLocal.get() == null ? 0 : likeService.getEntityLikeStatus(UserLocal.get().getId(), ENTITY_TYPE_POST, comment.getId());
                commentVO.put("likeStatus", likeStatus);
                //rvo 获取评论的评论
                List<Comment> replayList = commentService.getCommentsByUserId(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replayVOList = new ArrayList<>();
                if (replayList != null){
                    for (Comment reply :replayList) {
                        Map<String, Object> replyVO = new HashMap<>();
                        replyVO.put("reply", reply);
                        replyVO.put("user", userService.getOneUser(String.valueOf(reply.getUserId())));
                        User target = reply.getTargetId() == 0 ? null : userService.getOneUser(String.valueOf(reply.getTargetId()));
                        replyVO.put("target", target);
                        likeCount = likeService.getEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeCount", likeCount);
                        likeStatus = UserLocal.get() == null ? 0 : likeService.getEntityLikeStatus(UserLocal.get().getId(), ENTITY_TYPE_POST, reply.getId());
                        replyVO.put("likeStatus", likeStatus);

                        replayVOList.add(replyVO);
                    }
                }
                commentVO.put("replys", replayVOList);
                //获取该评论下面所有的回复数目
                int replyCount = commentService.getCommentCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("replyCount", replyCount);
                commentVOList.add(commentVO);
            }
        }
        model.addAttribute("comments", commentVOList);
        return "site/discuss-detail";

    }


}
