package com.example.HexaClientSpace.security;

import com.example.HexaClientSpace.entity.User;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Petit helper pour recuperer l'utilisateur authentifie sans repeter
 * le meme bloc SecurityContextHolder dans chaque controleur.
 */
@Component
public class CurrentUser {

    public User get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new IllegalStateException("Aucun utilisateur authentifie.");
        }
        return user;
    }

    public Long getId() {
        return get().getId();
    }
}
