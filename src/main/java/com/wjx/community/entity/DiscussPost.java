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
public class DiscussPost {

    private Integer id;

    private String userId;

    private String title;

    private String content;
    /**类型 0-普通，1-置顶*/
    private Integer type;

    private Integer status;

    private Date createTime;
    /** 评论数量*/
    private Integer commentCount;

    private Double score;

    @Override
    public String toString() {
        return "DiscussPost{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", context='" + content + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", createTime=" + createTime +
                ", commentCount=" + commentCount +
                ", score=" + score +
                '}';
    }
}
