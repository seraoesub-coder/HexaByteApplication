export interface Message {
  id: number;
  role: 'USER' | 'ASSISTANT' | 'SUPPORT';
  contenu: string;
  dateCreation: string;
  note: number | null;          // note du client (1 à 5) si la réponse a été évaluée
}

/** Un tour de dialogue : la question, la réponse de l'agent et le statut courant de la réclamation. */
export interface Echange {
  question: Message;
  reponse: Message;
  statutReclamation: string;
}

export interface EvaluationRequest {
  note: number;                 // 1 à 5
  commentaire?: string;         // 500 caractères max
}

export interface Evaluation {
  id: number;
  messageId: number;
  note: number;
  commentaire: string | null;
  dateEvaluation: string;
  statutReclamation: string;    // passe à ESCALADEE_SUPPORT si note <= 2
}