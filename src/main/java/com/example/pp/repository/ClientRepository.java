package com.example.pp.repository;

import com.example.pp.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("SELECT c FROM Client c WHERE c.phone = :phone")
    Optional<Client> findByPhone(@Param("phone") String phone);

    List<Client> findByMessageSendFalse();
}
