package com.example.pp.controller;

import com.example.pp.entity.ClientInfo;
import com.example.pp.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private ClientService clientService;
    @InjectMocks
    private UserController userController;

    private ClientInfo clientInfo1;
    private ClientInfo clientInfo2;
    private List<ClientInfo> clientsInfo;

    @BeforeEach
    void setUp() {
        clientInfo1 = new ClientInfo();
        clientInfo1.setName("Иван");
        clientInfo1.setSurname("Иванов");
        clientInfo1.setMiddleName("Иванович");
        clientInfo1.setPhone("1234567");
        clientInfo1.setBirthday(LocalDate.of(2000, 1, 1));

        clientInfo2 = new ClientInfo();
        clientInfo2.setName("Петр");
        clientInfo2.setSurname("Петров");
        clientInfo2.setMiddleName("Петрович");
        clientInfo2.setPhone("1234577");
        clientInfo2.setBirthday(LocalDate.of(2000, 2, 1));

        clientsInfo = List.of(clientInfo1, clientInfo2);
    }

    @Test
    void testGetAll_ReturnListOfClients() {
        when(clientService.getAllClients()).thenReturn(clientsInfo);

        List<ClientInfo> allClients = userController.getAllClients();

        assertNotNull(allClients);
        assertEquals(clientsInfo.size(), allClients.size());
        assertEquals(clientsInfo.get(0).getName(), allClients.get(0).getName());
        assertEquals(clientsInfo.get(0).getPhone(), allClients.get(0).getPhone());
        assertEquals(clientsInfo.get(1).getName(), allClients.get(1).getName());
        assertEquals(clientsInfo.get(1).getPhone(), allClients.get(1).getPhone());
        verify(clientService, times(1)).getAllClients();
    }

    @Test
    void testGetClientById_ClientExist() {
        when(clientService.getClientById(any())).thenReturn(ResponseEntity.ok(clientInfo1));

        ResponseEntity<ClientInfo> clientInfo = userController.getClientById(any());

        assertNotNull(clientInfo);
        assertEquals(clientInfo1, clientInfo.getBody());
        assertEquals(HttpStatus.OK, clientInfo.getStatusCode());
        assertEquals(clientInfo1.getPhone(), clientInfo.getBody().getPhone());
        assertEquals(clientInfo1.getName(), clientInfo.getBody().getName());
        verify(clientService, times(1)).getClientById(any());
    }

    @Test
    void testGetClientById_ClientNotFound() {
        when(clientService.getClientById(any())).thenReturn(ResponseEntity.notFound().build());

        ResponseEntity<ClientInfo> clientInfo = userController.getClientById(any());

        assertNotNull(clientInfo);
        assertEquals(HttpStatus.NOT_FOUND, clientInfo.getStatusCode());
        verify(clientService, times(1)).getClientById(any());
    }

    @Test
    void testGetClientById_ClientBadRequest() {
        when(clientService.getClientById(any())).thenReturn(ResponseEntity.badRequest().build());

        ResponseEntity<ClientInfo> clientInfo = userController.getClientById(any());

        assertNotNull(clientInfo);
        assertEquals(HttpStatus.BAD_REQUEST, clientInfo.getStatusCode());
        verify(clientService, times(1)).getClientById(any());
    }
}