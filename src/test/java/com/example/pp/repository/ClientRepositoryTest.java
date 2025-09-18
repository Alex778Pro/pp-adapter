package com.example.pp.repository;

import com.example.pp.entity.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ClientRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ClientRepository clientRepository;

    @Test
    void testFindByPhone_WhenClientExists() {
        Client client = new Client();
        client.setPhone("+79996784567");
        client.setFullName("Иванович Иван Иванов");
        client.setMessageSend(false);

        entityManager.persist(client);
        entityManager.flush();

        Optional<Client> found = clientRepository.findByPhone("+79996784567");

        assertTrue(found.isPresent());
        assertEquals("+79996784567", found.get().getPhone());
        assertEquals("Иванович Иван Иванов", found.get().getFullName());
    }

    @Test
    void testFindByPhone_WhenClientNotExists() {
        Optional<Client> found = clientRepository.findByPhone("+79990000000");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByMessageSendFalse_WhenClientsExist() {
        Client client = new Client();
        client.setPhone("+79996784567");
        client.setMessageSend(false);

        Client client1 = new Client();
        client1.setPhone("+79996684567");
        client1.setMessageSend(true);

        Client client2 = new Client();
        client2.setPhone("+79996884567");
        client2.setMessageSend(false);

        entityManager.persist(client);
        entityManager.persist(client1);
        entityManager.persist(client2);
        entityManager.flush();

        List<Client> found = clientRepository.findByMessageSendFalse();

        assertEquals(2, found.size());
        assertFalse(found.stream().allMatch(Client::isMessageSend));
    }

    @Test
    void testFindByMessageSendFalse_NoClients() {
        List<Client> found = clientRepository.findByMessageSendFalse();
        assertTrue(found.isEmpty());
    }

    @Test
    void testSaveClient() {
        Client client = new Client();
        client.setPhone("+79996784567");
        client.setFullName("Иванович Иван Иванов");
        client.setMessageSend(false);

        Client saved = clientRepository.save(client);
        Optional<Client> found = clientRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("+79996784567", found.get().getPhone());
        assertEquals("Иванович Иван Иванов", found.get().getFullName());
    }
}