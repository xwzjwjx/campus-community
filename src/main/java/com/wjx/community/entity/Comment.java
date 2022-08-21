package com.wjx.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author wjx
 * @description 评论相关
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private Integer id;

    private Integer userId;

    private Integer entityType;

    private Integer entityId;

    private Integer targetId;

    private String content;

    private Integer status;

    private Date createTime;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
