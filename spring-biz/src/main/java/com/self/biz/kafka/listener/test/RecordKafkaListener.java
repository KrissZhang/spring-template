package com.self.biz.kafka.listener.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.self.biz.event.handle.test.TestEvent;
import com.self.common.api.req.kafka.TestKafkaReq;
import com.self.common.constants.TopicConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class RecordKafkaListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @KafkaListener(topics = {TopicConstants.APP_TOPIC_TEST_RECORD})
    public void listen(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack) throws JsonProcessingException {
        ack.acknowledge();

        TestKafkaReq req = objectMapper.readValue(consumerRecord.value(), TestKafkaReq.class);

        //发布事件
        applicationEventPublisher.publishEvent(new TestEvent(req.getMsg()));
    }

}
