package com.example.HexaClientSpace.repository;

import com.example.HexaClientSpace.entity.StatutRendezVous;
import com.example.HexaClientSpace.entity.Technicien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TechnicienRepository extends JpaRepository<Technicien, Long> {

    /** Techniciens actifs sans rendez-vous déjà CONFIRME sur ce créneau. */
    @Query("select t from Technicien t where t.actif = true and not exists ("
         + "select 1 from RendezVous r where r.technicien = t "
         + "and r.statut = :statut and r.dateHeure = :dateHeure)")
    List<Technicien> findDisponibles(@Param("dateHeure") LocalDateTime dateHeure,
                                     @Param("statut") StatutRendezVous statut);

    default List<Technicien> disponiblesLe(LocalDateTime dateHeure) {
        return findDisponibles(dateHeure, StatutRendezVous.CONFIRME);
    }
}