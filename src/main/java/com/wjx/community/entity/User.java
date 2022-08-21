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
public class User {

    private Integer id;

    private String username;

    private String password;
    /**加密盐*/
    private String salt;

    private String email;
    /**'0-普通用户; 1-超级管理员; 2-版主;'*/
    private Integer type;
    /**0-未激活; 1-已激活*/
    private Integer status;
    /**激活码*/
    private String activationCode;
    /**头像*/
    private String headerUrl;

    private Date createTime;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", activationCode='" + activationCode + '\'' +
                ", headerUrl='" + headerUrl + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
