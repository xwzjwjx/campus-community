package com.wjx.community.dao;

import com.wjx.community.entity.DiscussPost;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wjx
 * @description
 */
@Repository
public interface DiscussPostMapper {
    Integer selectAllDPRows(int userId);

    List<DiscussPost> selectAll(int userId, int offset, int limit);

    Integer add(DiscussPost discussPost);

    DiscussPost selectOneById(String discussPostId);

    void updateScore(int postId, double score);
}
