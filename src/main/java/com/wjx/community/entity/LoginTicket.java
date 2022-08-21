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
@AllArgsConstructor
@NoArgsConstructor
public class LoginTicket {

    private Integer id;

    private Integer userId;

    /**登录凭证*/
    private String ticket;

    private Date expired;
}
