package com.example.pp.controller;

import com.example.pp.entity.ClientsInfo;
import com.example.pp.service.UserService;
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
    private final UserService userService;

    @GetMapping
    public List<ClientsInfo> getAllClients() {
        return userService.getAllClients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientsInfo> getClientById(@PathVariable("id") String clientId) {
        return userService.getClientById(clientId);
    }
}
