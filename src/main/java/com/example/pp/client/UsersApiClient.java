package com.example.pp.client;

import com.example.pp.config.FeignConfig;
import com.example.pp.entity.ClientInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "usersApiClient", url = "${spring.api.url}", configuration = FeignConfig.class)
public interface UsersApiClient {
    @PostMapping("/api/v1/getClient")
    List<ClientInfo> getClients();

    @PostMapping("/api/v1/getClient/{id}")
    ClientInfo getClientById(@PathVariable("id") String clientId);

}
