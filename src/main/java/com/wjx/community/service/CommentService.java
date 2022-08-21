package com.wjx.community.service;

import com.wjx.community.entity.Comment;

import java.util.List;

/**
 * @author wjx
 * @description
 */
public interface CommentService {
    /**
     * 获取帖子的所有评论
     * @param entityTypePost
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> getAllCommnetByEntity(int entityTypePost, Integer entityId, int offset, int limit);

    /**
     * 获取评论的评论
     * @param entityTypeComment
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> getCommentsByUserId(int entityTypeComment, Integer userId, int offset, int limit);

    /**
     * 获取总评论数
     * @param entityTypeComment
     * @param commentId
     * @return
     */
    int getCommentCountByEntity(int entityTypeComment, Integer commentId);

    /**
     * 添加一条评论
     * @param comment
     */
    void addComment(Comment comment);

    /**
     * 获取评论
     * @param commentId
     * @return
     */
    Comment getCommentById(Integer commentId);
}
