package com.example.pp.service;

import com.example.pp.entity.SmsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmsMessageKafkaService {
    @Value("${spring.kafka.template.default-topic}")
    private String defaultTopic;

    private KafkaTemplate<String, SmsMessage> kafkaTemplate;

    public void sendMessage(SmsMessage message) {
        kafkaTemplate.send(defaultTopic, message);
        log.info("SMS Sent to topic: " + defaultTopic);
        log.info("SMS Sent to topic: " + message);
    }
}
