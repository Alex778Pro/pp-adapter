package com.example.pp.service;

import com.example.pp.entity.Client;
import com.example.pp.entity.ClientInfo;
import com.example.pp.entity.SmsMessage;
import com.example.pp.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmsMessageKafkaService {
    @Value("${spring.kafka.template.default-topic}")
    private String defaultTopic;
    @Value("${spring.kafka.range-time-hour}")
    int rangeHour;
    @Value("${spring.sms.discount}")
    int discount;

    private final KafkaTemplate<String, SmsMessage> kafkaTemplate;
    private final ClientRepository clientRepository;

    private void sendMessage(SmsMessage message) {
        kafkaTemplate.send(defaultTopic, message);
    }
    public void sendNotificationIfAllowed(ClientInfo clientInfo) {
        if(isRangeTime()){
            sendSMSNotification(clientInfo);
            markClientAsNotified(clientInfo.getPhone());
        }
    }
    //Notification kafka
    private void sendSMSNotification(ClientInfo clientInfo) {
        String message = String.format("%s %s, в этом месяце для вас действует скидка %d%%",
                clientInfo.getName(), clientInfo.getMiddleName(), discount);

        SmsMessage sms = new SmsMessage(clientInfo.getPhone(), message);

        try {
            sendMessage(sms);
            log.info("SMS sent to phone: {}", clientInfo.getPhone());
        } catch (Exception e) {
            log.error("Error sending SMS to phone: {}", clientInfo.getPhone(), e);
        }
    }
    //Mark notification
    private void markClientAsNotified(String phone) {
        Optional<Client> client = clientRepository.findByPhone(phone);
        if (client.isPresent()) {
            client.get().setMessageSend(true);
            clientRepository.save(client.get());
        }
    }
    //Range time
    private boolean isRangeTime() {
        int hour = LocalDate.now().atStartOfDay().getHour();
        return hour < rangeHour;
    }
}
