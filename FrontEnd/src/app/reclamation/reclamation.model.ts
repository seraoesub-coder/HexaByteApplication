/** Corps EXACT de POST /api/reclamations. La catégorie se déduit de la sous-catégorie. */
export interface CreateReclamationRequest {
  sousCategorieId: number;
  description: string;          // 10 à 2000 caractères
  urgent?: boolean;             // true = priorité HAUTE, transmise directement à un conseiller humain
}

export interface Reclamation {
  id: number;
  reference: string;            // ex. "REC-2026-000001"
  statut: 'OUVERTE' | 'EN_COURS_IA' | 'RESOLUE_PAR_IA' | 'ESCALADEE_SUPPORT' | 'CLOTUREE';
  priorite: 'BASSE' | 'NORMALE' | 'HAUTE';
  description: string;
  categorieCode: string;
  categorieLibelle: string;
  sousCategorieCode: string;
  sousCategorieLibelle: string;
  dateCreation: string;         // ISO 8601
  dateMiseAJour: string;
}

/** Une ligne du journal des changements de statut. */
export interface HistoriqueStatut {
  ancienStatut: string | null;
  nouveauStatut: string;
  acteur: 'SYSTEME' | 'CLIENT' | 'SUPPORT';
  motif: string | null;
  dateChangement: string;
}