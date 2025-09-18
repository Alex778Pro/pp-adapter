package com.example.pp.mapper;

import com.example.pp.entity.Client;
import com.example.pp.entity.ClientInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ClientMapperTest {

    private final ClientMapper clientMapper = Mappers.getMapper(ClientMapper.class);

    private ClientInfo clientInfo;
    private Client client;

    @BeforeEach
    void setUp() {
        clientInfo = new ClientInfo();
        clientInfo.setName("Иван");
        clientInfo.setSurname("Иванов");
        clientInfo.setMiddleName("Иванович");
        clientInfo.setPhone("1234567");
        clientInfo.setBirthday(LocalDate.of(2000, 1, 1));

        client = new Client();
        client.setPhone("1234567");
        client.setFullName("Иванов Иван Иванович");
        client.setBirthDate(LocalDate.of(2000, 1, 1));
        client.setMessageSend(false);
    }

    @Test
    void TestToClient() {
        Client clientRezult = clientMapper.toClient(clientInfo);

        assertNotNull(clientRezult, "Клиент не может быть null");
        assertEquals(client.getFullName(), clientRezult.getFullName());
        assertEquals(client.getPhone(), clientRezult.getPhone());
        assertEquals(client.getBirthDate(), clientRezult.getBirthDate());
    }

    @Test
    void TestGetFullName() {
        String fullName = clientMapper.getFullName(clientInfo);

        assertNotNull(fullName, "Полное имя не может быть null");
        assertEquals(client.getFullName(), fullName);
    }
}