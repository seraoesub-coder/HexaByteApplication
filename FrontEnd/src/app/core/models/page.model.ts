/** Page renvoyée par les listes paginées du backend (Spring Data). */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;      // numéro de page courant (commence à 0)
  size: number;
  first: boolean;
  last: boolean;
}