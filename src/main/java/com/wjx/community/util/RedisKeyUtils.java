package com.wjx.community.util;

import java.util.UUID;

/**
 * @author wjx
 * @description 生成相关Redis key工具类
 */
public class RedisKeyUtils {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_POST = "post";
    private static final String PREFIX_CODE = "code";


    /**
     * kaptcha相关
     * @param owner
     * @return
     */
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
    }

    /**
     * login token相关
     * @param id
     * @return
     */
    public static String getToken(Integer id) {
        return UUID.randomUUID().toString().substring(2,8)+SPLIT+String.valueOf(id);
    }

    public static String getLoginKey(Integer id) {
        return PREFIX_TICKET+SPLIT+id;
    }

    public static String getEmailKey(String email) {
        return PREFIX_CODE+SPLIT+email;
    }

    /**
     * 实体集的key
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE+SPLIT+entityId+SPLIT+entityType;
    }

    /**
     * 用户所点赞的实体key
     * @param entityUserId 实体id
     * @return
     */
    public static String getUserLikeKey(int entityUserId) {
        return PREFIX_USER_LIKE+SPLIT+entityUserId;
    }

    /**
     * 帖子评分
     * @return
     */
    public static String getPostScoreKey() {
        return PREFIX_POST+SPLIT+"score";
    }

    /**
     * user -> entity 点关注
     * @param userId
     * @param entityType
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    /**
     * 该entity的粉丝（一般指人）
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }
}
