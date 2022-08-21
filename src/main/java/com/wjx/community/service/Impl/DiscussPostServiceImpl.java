package com.wjx.community.service.Impl;

import com.wjx.community.common.SensitiveFilter;
import com.wjx.community.dao.DiscussPostMapper;
import com.wjx.community.dao.UserMapper;
import com.wjx.community.entity.DiscussPost;
import com.wjx.community.entity.User;
import com.wjx.community.service.DiscussPostService;
import com.wjx.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wjx.community.common.CommunityConstant.ENTITY_TYPE_POST;

/**
 * @author wjx
 * @description
 */
@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Resource
    private DiscussPostMapper discussPostMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private LikeService likeService;
    @Resource
    private SensitiveFilter sensitiveFilter;

    @Override
    public int getAllDiscussPostRows(int userId) {
        return discussPostMapper.selectAllDPRows(userId);
    }

    @Override
    public List<Map<String, Object>> findAllDiscussPosts(int userId, int offset, int limit) {
        List<DiscussPost> discussPostList = discussPostMapper.selectAll(userId,offset,limit);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (discussPostList != null) {
            for (DiscussPost post: discussPostList) {
                Map<String, Object> map = new HashMap<>(8);
                map.put("post", post);
                map.put("user", userMapper.findUserById(post.getUserId()));
                long likeCount = likeService.getEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        return discussPosts;
    }

    @Override
    public boolean add(String title, String content, User user) {
        DiscussPost discussPost = new DiscussPost();
        //过滤敏感词
        discussPost.setContent(sensitiveFilter.filter(content));
        discussPost.setTitle(sensitiveFilter.filter(title));
        discussPost.setCommentCount(0);
        discussPost.setScore(0d);
        discussPost.setStatus(0);
        discussPost.setType(0);
        discussPost.setUserId(String.valueOf(user.getId()));
        Integer res = discussPostMapper.add(discussPost);
        if (res.equals(1)){
            //todo (发布帖子，将帖子异步提交到ES服务器)
            return true;
        }
        return false;
    }

    @Override
    public DiscussPost getOneDiscussPost(String discussPostId) {
        if (StringUtils.isBlank(discussPostId)){
            new IllegalArgumentException("帖子ID不能为空");
        }
        DiscussPost discussPost = discussPostMapper.selectOneById(discussPostId);
        if (discussPost == null){
            new IllegalArgumentException("该帖子不存在");
        }
        return discussPost;
    }

    @Override
    public void updateScore(int postId, double score) {
        discussPostMapper.updateScore(postId,score);
        return;
    }
}
