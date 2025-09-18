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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmsMessageKafkaServiceTest {
    @Mock
    private KafkaTemplate<String, SmsMessage> kafkaTemplate;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private LocalDate localDate;
    @Mock
    private ClientInfoMapper clientInfoMapper;
    @InjectMocks
    private SmsMessageKafkaService smsMessageKafkaService;

    private static final String TEST_PHONE = "+79991234567";
    private static final String TEST_NAME = "Иван";
    private static final String TEST_SURNAME = "Иванов";
    private static final String TEST_MIDDLE_NAME = "Иванович";
    private static final String TEST_FULL_NAME = TEST_NAME + " " + TEST_SURNAME + " " + TEST_MIDDLE_NAME;
    private static final String DEFAULT_TOPIC = "sms-topic";
    private static final int RANGE_HOUR = 19;
    private static final int DISCOUNT = 10;

    private Client testClient;
    private ClientInfo testClientInfo;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(smsMessageKafkaService, "defaultTopic", DEFAULT_TOPIC);
        ReflectionTestUtils.setField(smsMessageKafkaService, "rangeHour", RANGE_HOUR);
        ReflectionTestUtils.setField(smsMessageKafkaService, "discount", DISCOUNT);

        testClient = new Client();
        testClient.setPhone(TEST_PHONE);
        testClient.setFullName(TEST_FULL_NAME);
        testClient.setMessageSend(false);

        testClientInfo = new ClientInfo();
        testClientInfo.setPhone(TEST_PHONE);
        testClientInfo.setName(TEST_NAME);
        testClientInfo.setSurname(TEST_SURNAME);
        testClientInfo.setMiddleName(TEST_MIDDLE_NAME);
    }

    @Test
    void testSendMessage_Success() {
        SmsMessage testSmsMessage = new SmsMessage(TEST_NAME, "Test message");

        smsMessageKafkaService.sendMessage(testSmsMessage);

        verify(kafkaTemplate).send(eq(DEFAULT_TOPIC), eq(testSmsMessage));
    }

    @Test
    void testSendSmsNotification_Success() {
        boolean rezult = smsMessageKafkaService.sendSMSNotification(testClientInfo);

        assertTrue(rezult);
        verify(kafkaTemplate).send(eq(DEFAULT_TOPIC), any(SmsMessage.class));
    }

    @Test
    void testSendSmsNotification_Exception() {
        doThrow(new RuntimeException()).when(kafkaTemplate).send(any(), any());
        boolean rezult = smsMessageKafkaService.sendSMSNotification(testClientInfo);

        assertFalse(rezult);
    }

    @Test
    void testMarkClientAsNotified_ClientExists() {
        when(clientRepository.findByPhone(TEST_PHONE)).thenReturn(Optional.of(testClient));

        smsMessageKafkaService.markClientAsNotified(TEST_PHONE);

        assertTrue(testClient.isMessageSend());
        verify(clientRepository, times(1)).save(testClient);
    }

    @Test
    void testMarkClientAsNotified_ClientNoExists() {
        when(clientRepository.findByPhone(TEST_PHONE)).thenReturn(Optional.empty());

        smsMessageKafkaService.markClientAsNotified(TEST_PHONE);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void testPendingNotificationClients_WithPendingClients() {
        when(clientRepository.findByMessageSendFalse()).thenReturn(List.of(testClient));
        when(clientInfoMapper.toClientInfo(testClient)).thenReturn(testClientInfo);
        when(clientRepository.findByPhone(TEST_PHONE)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(testClient)).thenReturn(testClient);

        smsMessageKafkaService.pendingNotificationClients();

        verify(kafkaTemplate).send(any(), any());
        verify(clientRepository).save(testClient);
        verify(clientInfoMapper).toClientInfo(testClient);
        verify(clientRepository).findByMessageSendFalse();
    }

    @Test
    void testPendingNotificationClients_NoPendingClients() {
        when(clientRepository.findByMessageSendFalse()).thenReturn(List.of());

        smsMessageKafkaService.pendingNotificationClients();

        verify(kafkaTemplate, never()).send(any(), any());
        verify(clientRepository, never()).save(any());
        verify(clientInfoMapper, never()).toClientInfo(any());
        verify(clientRepository).findByMessageSendFalse();
    }

    @Test
    void testPendingNotificationClients_ClientInfoNull() {
        when(clientRepository.findByMessageSendFalse()).thenReturn(List.of(testClient));
        when(clientInfoMapper.toClientInfo(testClient)).thenReturn(null);

        smsMessageKafkaService.pendingNotificationClients();

        verify(kafkaTemplate, never()).send(any(), any());
        verify(clientRepository, never()).save(any());
        verify(clientInfoMapper).toClientInfo(testClient);
        verify(clientRepository).findByMessageSendFalse();
    }

    @Test
    void testSmsMassageContext(){
        boolean rezult = smsMessageKafkaService.sendSMSNotification(testClientInfo);

        assertTrue(rezult);
        verify(kafkaTemplate).send(eq(DEFAULT_TOPIC), argThat(sms ->
                sms.getMessage().equals("Иван Иванов, в этом месяце для вас действует скидка 10%") &&
                        sms.getPhone().equals(TEST_PHONE)
        ));
    }
}