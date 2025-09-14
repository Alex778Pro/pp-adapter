package com.example.pp.service;

import com.example.pp.client.UsersApiClient;
import com.example.pp.entity.Client;
import com.example.pp.entity.ClientInfo;
import com.example.pp.mapper.ClientMapper;
import com.example.pp.repository.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    @Mock
    private UsersApiClient usersApiClient;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SmsMessageKafkaService smsMessageKafkaService;
    @Mock
    private ClientMapper clientMapper;
    @InjectMocks
    private ClientService clientService;


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
    void getAllClientsReturnFiltresClients() {
        List<ClientInfo> clientInfoList = List.of(validClientInfo, invalidClientInfo);

        when(usersApiClient.getClients()).thenReturn(clientInfoList);
        when(clientRepository.findByPhone(any())).thenReturn(Optional.empty());
        when(clientRepository.save(any())).thenReturn(client);

        List<ClientInfo> result = clientService.getAllClients();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(validClientInfo, result.get(0));
        verify(usersApiClient, Mockito.times(1)).getClients();
        verify(clientRepository, Mockito.times(1)).save(any());
        verify(smsMessageKafkaService, Mockito.times(1)).sendNotificationIfAllowed(any());
        verify(smsMessageKafkaService, Mockito.times(1)).pendingNotificationClients();
    }

    @Test
    void getAllClients_ClientExists() {
        List<ClientInfo> clientInfoList = List.of(validClientInfo);

        when(usersApiClient.getClients()).thenReturn(clientInfoList);
        when(clientRepository.findByPhone(any())).thenReturn(Optional.of(client));

        List<ClientInfo> result = clientService.getAllClients();

        assertEquals(1, result.size());

        verify(clientRepository, Mockito.never()).save(any());
        verify(smsMessageKafkaService, Mockito.never()).sendNotificationIfAllowed(any());
        verify(smsMessageKafkaService, Mockito.times(1)).pendingNotificationClients();
    }

    @Test
    void getAllClients_KafkaThrowException() {
        List<ClientInfo> clientInfoList = List.of(validClientInfo);

        when(usersApiClient.getClients()).thenReturn(clientInfoList);
        when(clientRepository.findByPhone(any())).thenReturn(Optional.empty());
        when(clientRepository.save(any())).thenReturn(client);

        doThrow(new RuntimeException("Kafka Error")).when(smsMessageKafkaService).sendNotificationIfAllowed(any());

        List<ClientInfo> result = clientService.getAllClients();

        Assertions.assertEquals(1, result.size());
        verify(smsMessageKafkaService, Mockito.times(1)).sendNotificationIfAllowed(any());
        verify(smsMessageKafkaService, Mockito.times(1)).pendingNotificationClients();
    }

    @Test
    void getClientByIdValitClient() {
        String clientId = "123-GVG";

        when(usersApiClient.getClientById(clientId)).thenReturn(validClientInfo);

        ResponseEntity<ClientInfo> rezult = clientService.getClientById(clientId);

        assertNotNull(rezult);
        assertEquals(HttpStatus.OK, rezult.getStatusCode());
        assertEquals(validClientInfo.getPhone(), rezult.getBody().getPhone());
        verify(usersApiClient, Mockito.times(1)).getClientById(clientId);
    }

    @Test
    void getClientByIdInvalidClient() {
        String clientId = "123-GVG";

        when(usersApiClient.getClientById(clientId)).thenReturn(invalidClientInfo);

        ResponseEntity<ClientInfo> rezult = clientService.getClientById(clientId);

        assertEquals(HttpStatus.NOT_FOUND, rezult.getStatusCode());
    }

    @Test
    void seveNewClient() {
        when(clientRepository.findByPhone(any())).thenReturn(Optional.empty());
        when(clientRepository.save(any())).thenReturn(client);

        Client clientRezult = clientService.saveClient(validClientInfo);

        assertNotNull(clientRezult);
        assertEquals(validClientInfo.getPhone(), clientRezult.getPhone());
        verify(clientRepository, Mockito.times(1)).save(any());

    }

    @Test
    void saveClientExist(){
        when(clientRepository.findByPhone(any())).thenReturn(Optional.of(client));
        when(clientMapper.toClient(any())).thenReturn(client);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {clientService.saveClient(validClientInfo);});

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(clientRepository, Mockito.never()).save(any());
    }

}
