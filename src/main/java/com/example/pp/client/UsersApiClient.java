package com.example.pp.client;

import com.example.pp.config.FeignConfig;
import com.example.pp.entity.ClientsInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "usersApiClient", url = "http://localhost:8081", configuration = FeignConfig.class)
public interface UsersApiClient {
    @PostMapping("/api/v1/getClient")
    List<ClientsInfo> getClient();

    @PostMapping("/api/v1/getClient/{id}")
    ClientsInfo getClientById(@PathVariable("id") String clientId);

}
