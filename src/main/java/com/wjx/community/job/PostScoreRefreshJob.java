package com.wjx.community.job;

import com.wjx.community.entity.DiscussPost;
import com.wjx.community.service.DiscussPostService;
import com.wjx.community.service.Impl.LikeService;
import com.wjx.community.util.RedisKeyUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.wjx.community.common.CommunityConstant.DATE_FORMAT;
import static com.wjx.community.common.CommunityConstant.ENTITY_TYPE_POST;

/**
 * @author wjx
 * @description 刷新排行榜帖子任务
 */
public class PostScoreRefreshJob implements Job {


    private static Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);


    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private LikeService likeService;

    //纪元
    private static  Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat(DATE_FORMAT).parse("2022-04-15 00:00:00");
        } catch (ParseException e) {
            logger.error("初始化纪元失败",e.getMessage());
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtils.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            logger.info("[任务取消] 没有需要刷新的帖子!");
            return;
        }

        logger.info("[任务开始] 正在刷新帖子分数: " + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕!");
    }
    //刷新帖子分数
    private void refresh(int postId) {
        DiscussPost post = discussPostService.getOneDiscussPost(String.valueOf(postId));

        if (post == null) {
            logger.error("该帖子不存在: id = " + postId);
            return;
        }

        // 是否精华
        boolean wonderful = post.getStatus() == 1;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        long likeCount = likeService.getEntityLikeCount(ENTITY_TYPE_POST, postId);

        // 计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数 = 帖子权重 + 距离天数,取10的对数防止数溢出
        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        // 更新帖子分数
        discussPostService.updateScore(postId, score);
        // 同步搜索数据
        post.setScore(score);
        //elasticsearchService.saveDiscussPost(post);
    }
}
