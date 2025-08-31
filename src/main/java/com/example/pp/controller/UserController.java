package com.example.pp.controller;

import com.example.pp.entity.ClientInfo;
import com.example.pp.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/clients")
public class UserController {
    private final ClientService userService;

    @GetMapping
    public List<ClientInfo> getAllClients() {
        return userService.getAllClients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientInfo> getClientById(@PathVariable("id") String clientId) {
        return userService.getClientById(clientId);
    }
}
