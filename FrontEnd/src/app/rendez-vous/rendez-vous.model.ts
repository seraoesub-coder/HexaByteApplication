/** Communiqué au client uniquement une fois le rendez-vous confirmé. */
export interface Technicien {
  nom: string;
  prenom: string;
  telephone: string;
  niveauExperience: number;     // de 1 à 5
}

export interface RendezVous {
  id: number;
  reference: string;            // ex. "RDV-2026-000001"
  statut: 'PROPOSE' | 'CONFIRME' | 'ANNULE' | 'REALISE';
  dateHeure: string;            // ISO 8601
  dureeMinutes: number;
  adresse: string;              // reprise automatiquement du profil du client
  motif: string | null;
  reclamationReference: string | null;
  technicien: Technicien | null;
  dateCreation: string;
  dateConfirmation: string | null;
}

/** Corps facultatif de POST /api/rendez-vous. */
export interface CreateRendezVousRequest {
  reclamationId?: number;
  motif?: string;
}