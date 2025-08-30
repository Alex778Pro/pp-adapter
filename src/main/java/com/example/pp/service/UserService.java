package com.example.pp.service;

import com.example.pp.client.UsersApiClient;
import com.example.pp.entity.Client;
import com.example.pp.entity.ClientsInfo;
import com.example.pp.mapper.ClientMapper;
import com.example.pp.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class UserService {
    private final UsersApiClient usersApiClient;
    private final ClientRepository clientRepository;

    public List<ClientsInfo> getAllClients() {
        List<ClientsInfo> clients = usersApiClient.getClients().stream()
                .filter(this::matchesFilter)
                .toList();
        for (ClientsInfo client : clients) {
            try {
                log.info("Save client: " + saveClient(client));
            } catch (ResponseStatusException e) {
                log.info("Save client failed: " + e.getMessage());
            }
        }
        return clients;
    }

    public ResponseEntity<ClientsInfo> getClientById(String clientId) {
        ClientsInfo client = usersApiClient.getClientById(clientId);
        if (matchesFilter(client)) {
            return ResponseEntity.ok(client);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @Transactional
    public Client saveClient(ClientsInfo clientInfo) {
        Client client = ClientMapper.INSTANCE.toClient(clientInfo);
        Optional<Client> existingClient = clientRepository.findByPhone(clientInfo.getPhone());
        if (!existingClient.isPresent()) {
            return clientRepository.save(client);
        } else {
            log.error("Client with Phone {} already exists", client.getPhone());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Client with Phone " + client.getPhone() + " already exists");
        }
    }

    //Filter
    private boolean matchesFilter(ClientsInfo client) {
        char lastCharPhoneClient = client.getPhone().charAt(client.getPhone().length() - 1);
        int birthdayMonth = client.getBirthday().getMonthValue();
        return lastCharPhoneClient == '7' && birthdayMonth == LocalDate.now().getMonthValue();
    }
}
