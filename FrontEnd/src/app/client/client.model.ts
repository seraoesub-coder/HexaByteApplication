/** Réponse de GET /api/clients/me. */
export interface ClientProfile {
  clientId: number;
  numeroClient: string;
  nom: string;
  prenom: string;
  telephone: string | null;
  adresseInstallation: string | null;
  gouvernorat: string | null;
  email: string;
}

/** Corps de PATCH /api/clients/me : tous les champs sont facultatifs. */
export interface UpdateProfileRequest {
  telephone?: string;
  adresseInstallation?: string;
  gouvernorat?: string;
}