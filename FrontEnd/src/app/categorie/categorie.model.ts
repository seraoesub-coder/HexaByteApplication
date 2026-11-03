export interface SousCategorie {
  id: number;
  code: string;
  libelle: string;
}

export interface Categorie {
  id: number;
  code: string;
  libelle: string;
  description: string | null;
  sousCategories: SousCategorie[];
}