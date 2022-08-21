package com.wjx.community.service.Impl;

import com.wjx.community.common.SensitiveFilter;
import com.wjx.community.dao.CommentMapper;
import com.wjx.community.entity.Comment;
import com.wjx.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wjx
 * @description 评论
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Comment> getAllCommnetByEntity(int entityTypePost, Integer postId, int offset, int limit) {
        return commentMapper.getAllCommentByPost(entityTypePost,postId,offset,limit);
    }

    @Override
    public List<Comment> getCommentsByUserId(int entityTypeComment, Integer userId, int offset, int limit) {
        return commentMapper.getAllCommentByUser(entityTypeComment,userId,offset,limit);
    }

    @Override
    public int getCommentCountByEntity(int entityTypeComment, Integer commentId) {
        int count = commentMapper.getCommentCount(entityTypeComment,commentId);
        return count;
    }

    @Override
    public void addComment(Comment comment) {
        if (comment == null){
            throw new IllegalArgumentException("评论为空");
        }
        // 添加评论,过滤敏感词
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        commentMapper.add(comment);
    }

    @Override
    public Comment getCommentById(Integer commentId) {
        if (commentId == null){
            throw new IllegalArgumentException("参数有误");
        }
        Comment comment = commentMapper.getCommentById(commentId);
        if (comment != null){
            return comment;
        }
        return null;
    }
}
