package com.self.biz.kafka.producer;

import com.self.common.api.req.kafka.TestKafkaReq;
import com.self.common.constants.TopicConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RecordKafkaProducer {

    private static Logger logger = LoggerFactory.getLogger(RecordKafkaProducer.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value(TopicConstants.APP_TOPIC_TEST_RECORD)
    private String testRecordTopic;

    public void send(TestKafkaReq req){
        try{
            kafkaTemplate.send(testRecordTopic, "TEST_RECORD", req);
        }catch (Exception e){
            logger.error("请求失败,topic:{},key:{},exception:{}", testRecordTopic, "TEST_RECORD", req);
        }
    }

}
