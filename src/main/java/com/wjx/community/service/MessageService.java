package com.wjx.community.service;

import com.wjx.community.entity.Message;

import java.util.List;

/**
 * @author wjx
 * @description
 */
public interface MessageService {
    /**
     * 获取当前用户的所有会话
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findConversations(int userId, int offset, int limit);

    /**
     * 获取当前用户的会话数目
     * @param userId
     * @return
     */
    int findConversationCount(int userId);

    /**
     * 获取该会话的所有私信
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findLetters(String conversationId, int offset, int limit);

    /**
     * 获取该会话的私信数目
     * @param conversationId
     * @return
     */
    int findLetterCount(String conversationId);

    /**
     * 获取该用户的未读私信数目
     * @param userId
     * @param conversationId
     * @return
     */
    int findLetterUnreadCount(int userId, String conversationId);

    /**
     * 添加一条信息
     * @param message
     * @return
     */
    int addMessage(Message message);

    /**
     * 更新为已读
     * @param ids
     * @return
     */
    int readMessage(List<Integer> ids);

    /**
     * 删除一条私信
     * @param id
     * @return
     */
    int removeLetterById(int id);

    /**
     * 获取该用户的相关通知（评论，点赞，关注）
     * @param userId
     * @param topic
     * @return
     */
    Message findLatestNotice(int userId, String topic);

    /**
     * 获取通知数目
     * @param userId
     * @param topic
     * @return
     */
    int findNoticeCount(int userId, String topic);

    /**
     * 获取未读的通知数目
     * @param userId
     * @param topic
     * @return
     */
    int findNoticeUnreadCount(int userId, String topic);

    /**
     * 获取所有的通知
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findNotices(int userId, String topic, int offset, int limit);
}
