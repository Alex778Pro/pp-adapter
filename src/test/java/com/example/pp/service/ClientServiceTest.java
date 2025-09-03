package com.example.pp.service;

import com.example.pp.client.UsersApiClient;
import com.example.pp.entity.Client;
import com.example.pp.entity.ClientInfo;
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

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    @Mock
    private UsersApiClient usersApiClient;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SmsMessageKafkaService smsMessageKafkaService;
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

        Mockito.when(usersApiClient.getClients()).thenReturn(clientInfoList);
        Mockito.when(clientRepository.findByPhone(any())).thenReturn(Optional.empty());
        Mockito.when(clientRepository.save(any())).thenReturn(client);

        List<ClientInfo> result = clientService.getAllClients();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(validClientInfo, result.get(0));
        Mockito.verify(usersApiClient, Mockito.times(1)).getClients();
        Mockito.verify(clientRepository, Mockito.times(1)).save(any());
        Mockito.verify(smsMessageKafkaService, Mockito.times(1)).sendNotificationIfAllowed(any());
        Mockito.verify(smsMessageKafkaService, Mockito.times(1)).pendingNotificationClients();
    }

    @Test
    void getAllClients_ClientExists() {
        List<ClientInfo> clientInfoList = List.of(validClientInfo);

        Mockito.when(usersApiClient.getClients()).thenReturn(clientInfoList);
        Mockito.when(clientRepository.findByPhone(any())).thenReturn(Optional.of(client));

        List<ClientInfo> result = clientService.getAllClients();

        // Assert
        Assertions.assertEquals(1, result.size());
        // Проверка, что результат содержит 1 клиент

        Mockito.verify(clientRepository, Mockito.never()).save(any());
        Mockito.verify(smsMessageKafkaService, Mockito.never()).sendNotificationIfAllowed(any());
        Mockito.verify(smsMessageKafkaService, Mockito.times(1)).pendingNotificationClients();
    }

    @Test
    void getAllClients_KafkaThrowException() {
        List<ClientInfo> clientInfoList = List.of(validClientInfo);

        Mockito.when(usersApiClient.getClients()).thenReturn(clientInfoList);
        Mockito.when(clientRepository.findByPhone(any())).thenReturn(Optional.empty());
        Mockito.when(clientRepository.save(any())).thenReturn(client);

        Mockito.doThrow(new RuntimeException("Kafka Error")).when(smsMessageKafkaService).sendNotificationIfAllowed(any());

        List<ClientInfo> result = clientService.getAllClients();

        Assertions.assertEquals(1, result.size());
        Mockito.verify(smsMessageKafkaService, Mockito.times(1)).sendNotificationIfAllowed(any());
        Mockito.verify(smsMessageKafkaService, Mockito.times(1)).pendingNotificationClients();
    }

    @Test
    void getClientByIdValitClient() {
        String clientId = "123-GVG";

        Mockito.when(usersApiClient.getClientById(clientId)).thenReturn(validClientInfo);

        ResponseEntity<ClientInfo> rezult = clientService.getClientById(clientId);

        Assertions.assertNotNull(rezult);
        Assertions.assertEquals(HttpStatus.OK, rezult.getStatusCode());
        Assertions.assertEquals(validClientInfo.getPhone(), rezult.getBody().getPhone());
        Mockito.verify(usersApiClient, Mockito.times(1)).getClientById(clientId);
    }

    @Test
    void getClientByIdInvalidClient() {
        String clientId = "123-GVG";

        Mockito.when(usersApiClient.getClientById(clientId)).thenReturn(invalidClientInfo);

        ResponseEntity<ClientInfo> rezult = clientService.getClientById(clientId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, rezult.getStatusCode());
    }

    @Test
    void seveNewClient() {
        Mockito.when(clientRepository.findByPhone(any())).thenReturn(Optional.empty());
        Mockito.when(clientRepository.save(any())).thenReturn(client);

        Client clientRezult = clientService.saveClient(validClientInfo);

        Assertions.assertNotNull(clientRezult);
        Assertions.assertEquals(validClientInfo.getPhone(), clientRezult.getPhone());
        Mockito.verify(clientRepository, Mockito.times(1)).save(any());

    }

    @Test
    void saveClientExist(){
        Mockito.when(clientRepository.findByPhone(any())).thenReturn(Optional.of(client));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {clientService.saveClient(validClientInfo);});

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        Mockito.verify(clientRepository, Mockito.never()).save(any());
    }

}
