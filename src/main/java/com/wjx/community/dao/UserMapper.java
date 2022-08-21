package com.wjx.community.dao;

import com.wjx.community.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @author wjx
 * @description
 */
@Repository
public interface UserMapper {
    /**
     * 根据用户名获取用户
     * @param username
     * @return
     */
    User selectUserByName(String username);

    User selectUserByEmail(String email);

    Integer insertUser(User userParam);
    
    User selectById(Integer id);

    void deleteById(Integer id);

    Integer updateStatus(Integer userId, int status);

    User findUserById(String userId);

    Integer resetPwd(String email, String password);


}
