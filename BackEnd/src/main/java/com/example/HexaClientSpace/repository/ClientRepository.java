package com.example.HexaClientSpace.repository;

import com.example.HexaClientSpace.entity.Client;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUserId(Long userId);

    Optional<Client> findByNumeroClient(String numeroClient);

    boolean existsByNumeroClient(String numeroClient);

    boolean existsByTelephone(String telephone);
}
