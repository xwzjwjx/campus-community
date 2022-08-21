package com.wjx.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author wjx
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private Integer id;

    private Integer fromId;

    private Integer toId;

    private String conversationId;

    private String content;
    /**消息状态：1-已读，0-未读，2-删除*/
    private Integer status;

    private Date createTime;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", conversationId='" + conversationId + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
