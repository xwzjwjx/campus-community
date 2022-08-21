package com.wjx.community.event;

import com.alibaba.fastjson.JSONObject;
import com.wjx.community.entity.Event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wjx
 * @description 生产者
 */
@Component
public class EventProducer {

    @Resource
    private KafkaTemplate kafkaTemplate;

    public void fireEvent(Event event){
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
