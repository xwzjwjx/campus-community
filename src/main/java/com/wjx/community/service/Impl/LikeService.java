package com.wjx.community.service.Impl;

import com.wjx.community.util.RedisKeyUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wjx
 * @description 点赞处理,存入Redis缓存
 */
@Service
public class LikeService {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 点赞操作
     *
     * @param userId       点赞用户的id
     * @param entityType   点赞实体的类型
     * @param entityId     点赞实体的id
     * @param entityUserId 被点赞实体的用户id
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 某帖子实体点赞集合的key
                String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
                // 某个用户累计的赞的key
                String userLikeKey = RedisKeyUtils.getUserLikeKey(entityUserId);
                // 查询entityLikeKey对应的集合中是否已经存在当前userId
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                // 开启redis事务
                operations.multi();
                // userId 已经存在，即当前用户已经为该帖子点过赞
                if (isMember) {
                    // 取消这个赞
                    operations.opsForSet().remove(entityLikeKey, userId);
                    // 被点赞用户获赞数减少1
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    // userId 不存在，即当前用户还没为该帖子点过赞，直接点赞即可
                    operations.opsForSet().add(entityLikeKey, userId);
                    // 被点赞用户获赞数增加1
                    operations.opsForValue().increment(userLikeKey);
                }
                // 执行redis事务
                return operations.exec();
            }
        });
    }

    /**
     * 查询某帖子实体点赞的数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long getEntityLikeCount(int entityType, int entityId) {
        // 某帖子实体点赞集合的key
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
        // 统计该帖子实体的点赞集合中的数据数量，即点赞数
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询某人对某帖子实体的点赞状态
     * 0未点赞/1已点赞
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int getEntityLikeStatus(int userId, int entityType, int entityId) {
        // 某帖子实体点赞集合的key
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    /**
     * 查询某个用户累计获得的赞
     *
     * @param userId
     * @return
     */
    public int getUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtils.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}
