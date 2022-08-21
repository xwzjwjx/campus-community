package com.wjx.community.dao;

import com.wjx.community.entity.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wjx
 * @description
 */
@Repository
public interface MessageMapper {
    int selectLetterUnreadCount(int userId, String conversationId);

    int selectLetterCount(String conversationId);

    int selectNoticeUnreadCount(int userId, String topic);

    List<Message> selectLetters(String conversationId, int offset, int limit);

    int insertMessage(Message message);

    int deleteLetterById(int id);

    Message selectLatestNotice(int userId, String topic);

    List<Message> selectConversations(int userId, int offset, int limit);

    int selectConversationCount(int userId);

    int updateStatus(List<Integer> ids, int i);

    int selectNoticeCount(int userId, String topic);

    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
