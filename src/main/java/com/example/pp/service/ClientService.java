package com.example.pp.service;

import com.example.pp.client.UsersApiClient;
import com.example.pp.entity.Client;
import com.example.pp.entity.ClientInfo;
import com.example.pp.mapper.ClientMapper;
import com.example.pp.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ClientService {
    private final UsersApiClient usersApiClient;
    private final ClientRepository clientRepository;
    private final SmsMessageKafkaService smsMessageKafkaService;

    //Get all clients Open Api
    public List<ClientInfo> getAllClients() {
        List<ClientInfo> clients = usersApiClient.getClients().stream()
                .filter(this::matchesFilter)
                .toList();
        for (ClientInfo client : clients) {
            try {
                log.info("Save client: " + saveClient(client));
                smsMessageKafkaService.sendNotificationIfAllowed(client);
            } catch (Exception e) {
                log.info("Save client failed: " + e.getMessage());
            }
            try {
                smsMessageKafkaService.pendingNotificationClients();
            }catch (Exception e) {
                log.info("Notification client failed: " + e.getMessage());
            }
        }
        return clients;
    }

    //Get client by clientID OpenApi
    public ResponseEntity<ClientInfo> getClientById(String clientId) {
        ClientInfo client = usersApiClient.getClientById(clientId);
        if (matchesFilter(client)) {
            return ResponseEntity.ok(client);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    //Save client is DB
    @Transactional
    public Client saveClient(ClientInfo clientInfo) {
        Client client = ClientMapper.INSTANCE.toClient(clientInfo);
        Optional<Client> existingClient = clientRepository.findByPhone(clientInfo.getPhone());
        if (existingClient.isEmpty()) {
            return clientRepository.save(client);
        } else {
            log.info("Client with Phone {} already exists", client.getPhone());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Client with Phone " + client.getPhone() + " already exists");
        }
    }

    //Filter
    private boolean matchesFilter(ClientInfo client) {
        char lastCharPhoneClient = client.getPhone().charAt(client.getPhone().length() - 1);
        int birthdayMonth = client.getBirthday().getMonthValue();
        return lastCharPhoneClient == '7' && birthdayMonth == LocalDate.now().getMonthValue();
    }
}
