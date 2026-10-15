package com.example.HexaClientSpace.dto;

/**
 * Reponse renvoyee apres login / inscription.
 * Le mot de passe n'apparait evidemment jamais ici.
 */
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long userId,
        String email,
        String role,
        String numeroClient,
        String nomComplet
) {
    public static AuthResponse of(String token, long expiresIn, Long userId, String email,
                                  String role, String numeroClient, String nomComplet) {
        return new AuthResponse(token, "Bearer", expiresIn, userId, email, role, numeroClient, nomComplet);
    }
}
