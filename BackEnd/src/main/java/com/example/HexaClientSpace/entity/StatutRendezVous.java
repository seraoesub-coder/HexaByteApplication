package com.example.HexaClientSpace.entity;

/**
 * PROPOSE -> CONFIRME -> REALISE
 *    |           |
 *    +-----------+--> ANNULE
 */
public enum StatutRendezVous {
    PROPOSE,
    CONFIRME,
    ANNULE,
    REALISE;

    public boolean peutPasserA(StatutRendezVous cible) {
        return switch (this) {
            case PROPOSE -> cible == CONFIRME || cible == ANNULE;
            case CONFIRME -> cible == ANNULE || cible == REALISE;
            case ANNULE, REALISE -> false;
        };
    }
}