package com.example.HexaClientSpace.repository;

import com.example.HexaClientSpace.entity.SousCategorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SousCategorieRepository extends JpaRepository<SousCategorie, Long> {

    Optional<SousCategorie> findByCode(String code);
}