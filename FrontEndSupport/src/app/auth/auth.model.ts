/** Corps EXACT attendu par POST /api/auth/register. */
export interface RegisterRequest {
  email: string;               // obligatoire, email valide, unique
  motDePasse: string;          // obligatoire, 8 caractères min, 1 majuscule, 1 chiffre
  nom: string;                 // obligatoire
  prenom: string;              // obligatoire
  telephone?: string;          // 8 chiffres, avec ou sans +216, unique
  adresseInstallation?: string;
  gouvernorat?: string;
}

/** Corps de POST /api/auth/login. */
export interface LoginRequest {
  email: string;
  motDePasse: string;
}

/** Réponse de register (201) et de login (200). */
export interface AuthResponse {
  accessToken: string;
  tokenType: string;           // "Bearer"
  expiresIn: number;           // secondes (3600)
  userId: number;
  email: string;
  role: string;                // "CLIENT", "AGENT_SUPPORT" ou "ADMIN"
  numeroClient: string | null; // ex. "HXB-2026-000001" (clients uniquement)
  nomComplet: string | null;
}