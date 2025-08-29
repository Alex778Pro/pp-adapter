package com.example.pp.service;

import com.example.pp.client.UsersApiClient;
import com.example.pp.entity.ClientsInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UsersApiClient usersApiClient;

    public List<ClientsInfo> getAllClients() {
        try {
            List<ClientsInfo> clients = usersApiClient.getClient().stream()
                    .filter(this::matchesFilter)
                    .toList();
            return clients;
        } catch (ResponseStatusException e) {
            throw e;
        }
    }

    public ClientsInfo getClientById(String clientId) {
        try {
            ClientsInfo client = usersApiClient.getClientById(clientId);
            if (matchesFilter(client)) {
                return client;
            }
        } catch (ResponseStatusException e) {
            throw e;
        }
        return null;
    }
    //Filter
    private boolean matchesFilter(ClientsInfo client) {
        char lastCharPhoneClient = client.getPhone().charAt(client.getPhone().length() - 1);
        int birthdayMonth = client.getBirthday().getMonthValue();
        return lastCharPhoneClient == '7' && birthdayMonth == LocalDate.now().getMonthValue();
    }
}
