package com.example.pp.service;

import com.example.pp.entity.SmsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {
    private final UserService userService;
    private final SmsMessageKafkaService smsMessageKafkaService;

    @Scheduled(cron = "0 */1 * * * *", zone = "Europe/Vilnius")
    public void processClients(){
        log.info("Processing Clients");
        //smsMessageKafkaService.sendMessage(new SmsMessage());
        userService.getAllClients();
    }
}
