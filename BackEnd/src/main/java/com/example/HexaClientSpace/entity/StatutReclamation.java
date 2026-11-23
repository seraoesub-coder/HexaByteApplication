package com.example.HexaClientSpace.entity;

/**
 * Cycle de vie d'une réclamation, avec les transitions autorisées :
 *
 * OUVERTE -> EN_COURS_IA -> RESOLUE_PAR_IA -> CLOTUREE
 *    |            |                |
 *    +------------+----------------+--> ESCALADEE_SUPPORT -> CLOTUREE
 *    +------------+----------------+-----------------------------------> ANNULEE
 */

public enum StatutReclamation {
    OUVERTE,
    ANNULEE,
    EN_COURS_IA,
    RESOLUE_PAR_IA,
    ESCALADEE_SUPPORT,
    CLOTUREE;

        public boolean peutPasserA(StatutReclamation cible) {
        if (cible == ANNULEE) {
            return this == OUVERTE || this == EN_COURS_IA || this == RESOLUE_PAR_IA || this == ESCALADEE_SUPPORT;
        }
        return switch (this) {
            case OUVERTE -> cible == EN_COURS_IA || cible == ESCALADEE_SUPPORT;
            // "Résolue par l'IA" n'est possible qu'après une vraie interaction avec l'agent.
            case ANNULEE -> false;
            case EN_COURS_IA -> cible == RESOLUE_PAR_IA || cible == ESCALADEE_SUPPORT;
            case RESOLUE_PAR_IA -> cible == CLOTUREE || cible == ESCALADEE_SUPPORT;
            case ESCALADEE_SUPPORT -> cible == CLOTUREE;
            case CLOTUREE -> false;
        };
    }
}