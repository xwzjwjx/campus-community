package com.wjx.community.dao;

import com.wjx.community.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wjx
 * @description
 */
@Repository
public interface CommentMapper {
    List<Comment> getAllCommentByPost(int entityType, Integer entityId, int offset, int limit);

    List<Comment> getAllCommentByUser(int entityType, Integer entityId, int offset, int limit);

    int getCommentCount(int entityTypeComment, Integer commentId);

    void add(Comment comment);

    Comment getCommentById(Integer commentId);
}
