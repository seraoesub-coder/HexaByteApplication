package com.example.HexaClientSpace.repository;

import com.example.HexaClientSpace.entity.EntreeCarnet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EntreeCarnetRepository extends JpaRepository<EntreeCarnet, Long> {

    /** Ordre chronologique : la plus ancienne entrée d'abord. */
    @Query("select e from EntreeCarnet e join fetch e.auteur where e.reclamation.id = :reclamationId order by e.id asc")
    List<EntreeCarnet> findByReclamationId(@Param("reclamationId") Long reclamationId);
}