package com.wjx.community.event;

import com.alibaba.fastjson.JSONObject;
import com.wjx.community.entity.DiscussPost;
import com.wjx.community.entity.Event;
import com.wjx.community.entity.Message;
import com.wjx.community.service.DiscussPostService;
import com.wjx.community.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.wjx.community.common.CommunityConstant.*;

/**
 * @author wjx
 * @description 消费者
 */
@Component
public class EventConsumer {
    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

//    @Autowired
//    private ElasticsearchService elasticsearchService;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    /**
     * 监听发表评论，点赞，关注相关的消息
     * @param consumerRecord
     */
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord consumerRecord) {
        if (consumerRecord == null || consumerRecord.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(consumerRecord.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return;
        }
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        //持久化到数据库
        messageService.addMessage(message);
    }

    /**
     * 帖子发布时
     * @param consumerRecord
     */
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord consumerRecord) {
        if (consumerRecord == null || consumerRecord.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(consumerRecord.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return;
        }
        DiscussPost discussPost = discussPostService.getOneDiscussPost(String.valueOf(event.getEntityId()));
        //将发布的帖子存入ES中
        //elasticsearchService.saveDiscussPost(discussPost);
    }
}
