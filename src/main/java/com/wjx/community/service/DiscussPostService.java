package com.wjx.community.service;

import com.wjx.community.entity.DiscussPost;
import com.wjx.community.entity.User;

import java.util.List;
import java.util.Map;

/**
 * @author wjx
 * @description
 */
public interface DiscussPostService {

    /**
     * 查询所有的discuss行数
     * @param userId
     * @return
     */
    int getAllDiscussPostRows(int userId);

    /**
     * 查询所有的discuss
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Map<String, Object>> findAllDiscussPosts(int userId, int offset, int limit);

    /**
     * 发布帖子
     * @param title
     * @param content
     * @param user
     * @return
     */
    boolean add(String title, String content, User user);

    DiscussPost getOneDiscussPost(String discussPostId);

    /**
     * 更新帖子分数
     * @param postId
     * @param score
     */
    void updateScore(int postId, double score);
}
