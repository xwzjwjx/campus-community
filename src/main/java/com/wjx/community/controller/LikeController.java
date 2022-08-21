package com.wjx.community.controller;

import com.wjx.community.common.UserLocal;
import com.wjx.community.entity.Event;
import com.wjx.community.entity.User;
import com.wjx.community.event.EventProducer;
import com.wjx.community.service.Impl.LikeService;
import com.wjx.community.util.ResponseUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.wjx.community.common.CommunityConstant.TOPIC_LIKE;

/**
 * @author wjx
 * @description 点赞相关
 */
@Controller
public class LikeController {

    @Resource
    private LikeService likeService;
    @Resource
    private EventProducer eventProducer;
    @PostMapping("/like")
    @ResponseBody
    public String like(Integer entityType, Integer entityId, Integer entityUserId, Integer postId) {
        User user = UserLocal.get();
        if (user == null) {
            return ResponseUtils.getJSONString(-1, "没有登录", null);
        }
        //点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        //获取点赞数量
        long likeCount = likeService.getEntityLikeCount(entityType, entityId);
        //该用户对该帖子的点赞状态
        int likeStatus = likeService.getEntityLikeStatus(user.getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>(8);
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        //不能为自己点赞,点过赞也不能点
        if (likeStatus == 1 && user.getId().equals(entityUserId) ) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(UserLocal.get().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }
        return ResponseUtils.getJSONString(0, null, map);
    }


}
