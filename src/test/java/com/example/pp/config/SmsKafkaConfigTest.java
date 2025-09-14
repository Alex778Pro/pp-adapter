package com.example.pp.config;

import com.example.pp.entity.SmsMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class SmsKafkaConfigTest {
    @Autowired
    private ProducerFactory<String, SmsMessage> producerFactory;

    @Test
    void testKafkaConfig() {
        assertNotNull(producerFactory, "Producer factory should be configured");
    }
}
