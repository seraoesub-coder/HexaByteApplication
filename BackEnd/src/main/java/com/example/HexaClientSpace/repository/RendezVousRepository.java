package com.example.HexaClientSpace.repository;

import com.example.HexaClientSpace.entity.RendezVous;
import com.example.HexaClientSpace.entity.StatutRendezVous;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    // ----- Côté client -----

    @Query(value = "select r from RendezVous r left join fetch r.technicien left join fetch r.reclamation "
                 + "where r.client.id = :clientId",
           countQuery = "select count(r) from RendezVous r where r.client.id = :clientId")
    Page<RendezVous> findByClientId(@Param("clientId") Long clientId, Pageable pageable);

    /** Filtre sur le client : le rendez-vous d'un autre client est simplement "introuvable". */
    @Query("select r from RendezVous r left join fetch r.technicien left join fetch r.reclamation "
         + "where r.id = :id and r.client.id = :clientId")
    Optional<RendezVous> findByIdAndClientId(@Param("id") Long id, @Param("clientId") Long clientId);

    boolean existsByClientIdAndStatutAndDateHeureAfter(Long clientId, StatutRendezVous statut,
                                                       LocalDateTime apres);

    // ----- Côté support -----

    @Query(value = "select r from RendezVous r left join fetch r.technicien left join fetch r.reclamation "
                 + "join fetch r.client where r.statut = :statut",
           countQuery = "select count(r) from RendezVous r where r.statut = :statut")
    Page<RendezVous> findByStatut(@Param("statut") StatutRendezVous statut, Pageable pageable);

    @Query("select r from RendezVous r left join fetch r.technicien left join fetch r.reclamation "
         + "join fetch r.client where r.id = :id")
    Optional<RendezVous> findDetailById(@Param("id") Long id);
}