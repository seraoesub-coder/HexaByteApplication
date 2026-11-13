/** Erreur simplifiée, prête à afficher dans un formulaire. */
export interface ApiError {
  message: string;                       // message général
  fieldErrors: Record<string, string>;   // ex. { email: "...", motDePasse: "..." }
}