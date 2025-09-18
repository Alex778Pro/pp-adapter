package com.example.pp.controller;

import com.example.pp.entity.ClientInfo;
import com.example.pp.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Client Controller", description = "\n" + "API-интерфейсы для управления пользователями")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/clients")
public class UserController {
    private final ClientService userService;

    @Operation(summary = "Получение всех клиентов", description = "Получаем всех клиентов по OpenApi, сохраняем их в базу данных и отправляем SMS уведомление через Kafka.")
    @GetMapping
    public List<ClientInfo> getAllClients() {
        return userService.getAllClients();
    }

    @Operation(summary = "Получаем клиента по ID", description = "Получаем клиента по ID через OpenApi")
    @GetMapping("/{id}")
    public ResponseEntity<ClientInfo> getClientById(@PathVariable("id") String clientId) {
        return userService.getClientById(clientId);
    }
}
