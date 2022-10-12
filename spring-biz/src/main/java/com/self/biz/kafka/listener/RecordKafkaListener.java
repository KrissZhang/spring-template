package com.self.biz.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.self.common.api.req.kafka.TestKafkaReq;
import com.self.common.constants.TopicConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class RecordKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(RecordKafkaListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = {TopicConstants.APP_TOPIC_TEST_RECORD})
    public void listen(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack) throws JsonProcessingException {
        ack.acknowledge();

        TestKafkaReq req = objectMapper.readValue(consumerRecord.value(), TestKafkaReq.class);

        logger.info("接收KafKa测试消息：" + req.getMsg());
    }

}
