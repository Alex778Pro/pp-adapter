package com.example.pp.mapper;

import com.example.pp.entity.Client;
import com.example.pp.entity.ClientInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ClientInfoMapperTest {
    private final ClientInfoMapper clientInfoMapper = Mappers.getMapper(ClientInfoMapper.class);

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
    void TestToClientInfo() {
        ClientInfo clientInfoRezult = clientInfoMapper.toClientInfo(client);

        assertNotNull(clientInfoRezult, "Клиент не может быть null");
        assertEquals(clientInfoRezult.getName(), clientInfo.getName());
        assertEquals(clientInfoRezult.getSurname(), clientInfo.getSurname());
        assertEquals(clientInfoRezult.getPhone(), clientInfo.getPhone());
    }

    @Test
    void TestGetNameClient() {
        String name = clientInfoMapper.getNameClient(client);

        assertNotNull(name);
        assertEquals(name, clientInfo.getName());
    }

    @Test
    void TestGetNameClient_ClientEmpty(){
        client.setFullName("");
        String name = clientInfoMapper.getNameClient(client);

        assertNotNull(name);
        assertEquals(name, "nullName");
    }

    @Test
    void TestGetSurnameClient() {
        String surname = clientInfoMapper.getSurnameClient(client);

        assertNotNull(surname);
        assertEquals(surname, clientInfo.getSurname());
    }

    @Test
    void TestGetSurnameClient_ClientEmpty(){
        client.setFullName("");
        String surname = clientInfoMapper.getSurnameClient(client);

        assertNotNull(surname);
        assertEquals(surname, "nullSurname");
    }
}