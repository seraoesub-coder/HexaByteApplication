/** Informations du compte connecté, conservées avec le token (pour l'affichage). */
export interface SessionUser {
  userId: number;
  email: string;
  role: string;                 // "CLIENT", "AGENT_SUPPORT" ou "ADMIN"
  numeroClient: string | null;  // ex. "HXB-2026-000001" (clients uniquement)
  nomComplet: string | null;
}