package com.example.pp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {
    private final ClientService userService;

    @Scheduled(cron = "0 0 * * * *", zone = "Europe/Moscow")
    public void processClients() {
        log.info("Processing Clients");
        userService.getAllClients();
    }
}
