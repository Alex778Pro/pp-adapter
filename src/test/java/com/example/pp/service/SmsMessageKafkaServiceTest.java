package com.example.pp.service;

import com.example.pp.entity.Client;
import com.example.pp.entity.ClientInfo;
import com.example.pp.entity.SmsMessage;
import com.example.pp.mapper.ClientInfoMapper;
import com.example.pp.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmsMessageKafkaServiceTest {
    @Mock
    private KafkaTemplate<String, SmsMessage> kafkaTemplate;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ClientInfoMapper clientInfoMapper;
    @InjectMocks
    private SmsMessageKafkaService smsMessageKafkaService;

    private ClientInfo validClientInfo;
    private ClientInfo invalidClientInfo;
    private Client client;

    @BeforeEach
    void setUp() {
        validClientInfo = new ClientInfo();
        validClientInfo.setPhone("1234567");
        validClientInfo.setBirthday(LocalDate.now());

        invalidClientInfo = new ClientInfo();
        invalidClientInfo.setPhone("12345689");
        invalidClientInfo.setBirthday(LocalDate.now().withMonth(1));

        client = new Client();
        client.setPhone("1234567");
        client.setBirthDate(LocalDate.now());
        client.setMessageSend(false);
    }

    @Test
    void sendNotificationIfAllowed() {
        when(smsMessageKafkaService.isRangeTime()).thenReturn(true);
        when(clientRepository.save(any())).thenReturn(client);
    }

    @Test
    void pendingNotificationClients() {
        when(smsMessageKafkaService.isRangeTime()).thenReturn(true);
        when(clientRepository.save(any())).thenReturn(client);

    }
    @Test
    void TestMarkClientAsNotified(){
        when(clientRepository.save(any())).thenReturn(client);
        when(clientRepository.findByPhone(client.getPhone())).thenReturn(Optional.ofNullable(client));

        smsMessageKafkaService.markClientAsNotified(client.getPhone());

        verify(clientRepository, times(1)).save(any());
        verify(clientRepository, times(1)).findByPhone(client.getPhone());
        assertEquals(client.isMessageSend(), true);
    }

    @Test
    void TestMarkClientAsNotifiedWithInvalidPhone(){
        when(clientRepository.findByPhone(client.getPhone())).thenReturn(Optional.empty());

        smsMessageKafkaService.markClientAsNotified(client.getPhone());

        verify(clientRepository, never()).save(any());
    }
}