package com.example.HexaClientSpace.repository;

import com.example.HexaClientSpace.entity.HistoriqueStatut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueStatutRepository extends JpaRepository<HistoriqueStatut, Long> {

    List<HistoriqueStatut> findByReclamationIdOrderByIdAsc(Long reclamationId);
}