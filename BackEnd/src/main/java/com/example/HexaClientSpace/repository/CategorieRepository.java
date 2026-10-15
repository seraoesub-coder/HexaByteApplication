package com.example.HexaClientSpace.repository;

import com.example.HexaClientSpace.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    /** Une seule requête : catégories actives avec leurs sous-catégories (évite le N+1). */
    @Query("select distinct c from Categorie c left join fetch c.sousCategories "
         + "where c.actif = true order by c.ordre asc")
    List<Categorie> findAllActivesAvecSousCategories();

    Optional<Categorie> findByCode(String code);
}