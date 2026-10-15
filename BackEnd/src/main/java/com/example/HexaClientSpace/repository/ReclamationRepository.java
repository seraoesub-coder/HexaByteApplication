package com.example.HexaClientSpace.repository;

import com.example.HexaClientSpace.entity.Reclamation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {

    @Query(value = "select r from Reclamation r join fetch r.sousCategorie s join fetch s.categorie "
                 + "where r.client.id = :clientId",
           countQuery = "select count(r) from Reclamation r where r.client.id = :clientId")
    Page<Reclamation> findByClientId(@Param("clientId") Long clientId, Pageable pageable);

    /** Filtre sur le client : une réclamation d'un autre client est simplement "introuvable". */
    @Query("select r from Reclamation r join fetch r.sousCategorie s join fetch s.categorie "
         + "where r.id = :id and r.client.id = :clientId")
    Optional<Reclamation> findByIdAndClientId(@Param("id") Long id, @Param("clientId") Long clientId);
}