import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_URL } from '../core/config/api.config';
import { Categorie } from './categorie.model';

/** Catégories et sous-catégories de réclamation. Correspond à CategorieController. */
@Injectable({ providedIn: 'root' })
export class CategorieService {

  constructor(private http: HttpClient) {}

  /** GET /api/categories : public, aucun token nécessaire. */
  lister(): Observable<Categorie[]> {
    return this.http.get<Categorie[]>(`${API_URL}/categories`);
  }
}