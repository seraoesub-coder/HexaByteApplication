package com.example.HexaClientSpace.entity;

/**
 * Rôles applicatifs. Spring Security préfixe par "ROLE_" quand on utilise
 * hasRole(...), donc on ne stocke ici que le nom court.
 */
public enum Role {
    CLIENT,
    AGENT_SUPPORT,
    ADMIN
}
