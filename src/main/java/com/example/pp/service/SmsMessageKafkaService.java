package com.example.pp.service;

import com.example.pp.entity.Client;
import com.example.pp.entity.ClientInfo;
import com.example.pp.entity.SmsMessage;
import com.example.pp.mapper.ClientInfoMapper;
import com.example.pp.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
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
    private final ClientInfoMapper clientInfoMapper;

    private void sendMessage(SmsMessage message) {
        kafkaTemplate.send(defaultTopic, message);
    }

    public void sendNotificationIfAllowed(ClientInfo clientInfo) {
        if (isRangeTime()) {
            boolean sentSuccessfully = sendSMSNotification(clientInfo);
            if (sentSuccessfully)
                markClientAsNotified(clientInfo.getPhone());
        }
    }

    //Pending client
    public void pendingNotificationClients() {
        log.info("Pending notification clients");
        List<Client> clients = clientRepository.findByMessageSendFalse();
        if (!clients.isEmpty()) {
            for (Client client : clients) {
                ClientInfo clientInfo = clientInfoMapper.toClientInfo(client);
                if (clientInfo != null) {
                    sendNotificationIfAllowed(clientInfo);
                    markClientAsNotified(clientInfo.getPhone());
                }
            }
        } else {
            log.info("No pending notification client found");
        }
    }


    //Notification kafka
    public boolean sendSMSNotification(ClientInfo clientInfo) {
        String message = String.format("%s %s, в этом месяце для вас действует скидка %d%%",
                clientInfo.getName(), clientInfo.getSurname(), discount);

        SmsMessage sms = new SmsMessage(clientInfo.getPhone(), message);

        try {
            sendMessage(sms);
            log.info("SMS sent to phone: {}", clientInfo.getPhone());
            return true;
        } catch (Exception e) {
            log.error("Error sending SMS to phone: {}", clientInfo.getPhone(), e);
        }
        return false;
    }

    //Mark notification
    public void markClientAsNotified(String phone) {
        Optional<Client> client = clientRepository.findByPhone(phone);
        if (client.isPresent()) {
            client.get().setMessageSend(true);
            clientRepository.save(client.get());
        }
    }

    //Range time
    public boolean isRangeTime() {
        int hour = LocalDate.now().atStartOfDay(ZoneId.of("Europe/Moscow")).getHour();
        return hour < rangeHour;
    }
}
