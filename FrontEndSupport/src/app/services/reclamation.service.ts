import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_URL } from '../core/config/api.config';
import { Page } from '../core/models/page.model';
import { CreateReclamationRequest, HistoriqueStatut, Reclamation} from './reclamation.model';

/**
 * Réclamations du client connecté. Correspond à ReclamationController
 * et ReclamationActionsController côté backend. Le token est ajouté par l'intercepteur.
 */
@Injectable({ providedIn: 'root' })
export class ReclamationService {

  private readonly url = `${API_URL}/support/reclamations`;


  constructor(private http: HttpClient) {}

  /** POST /api/reclamations -> 201 Created, statut OUVERTE. */
  creer(request: CreateReclamationRequest): Observable<Reclamation> {
    return this.http.post<Reclamation>(this.url, request);
  }

  /** GET /api/reclamations?page=0&size=10 : les plus récentes d'abord. */
  lister(page = 0, size = 10): Observable<Page<Reclamation>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Reclamation>>(this.url, { params });
  }

// Récupère directement un tableau brut de réclamations
  toutesPourSupport(statut: string): Observable<Reclamation[]> {
    const params = new HttpParams()
    return this.http.get<Reclamation[]>(this.url, { params });
  }



  /** GET /api/reclamations/{id} : 404 si elle n'appartient pas au client. */
  obtenir(id: number): Observable<Reclamation> {
    return this.http.get<Reclamation>(`${this.url}/${id}`);
  }

  /** POST /api/reclamations/{id}/escalade : demande un conseiller humain (motif facultatif). */
  escalader(id: number, motif?: string): Observable<Reclamation> {
    return this.http.post<Reclamation>(`${this.url}/${id}/escalade`, motif ? { motif } : null);
  }

  /** POST /api/reclamations/{id}/resolution : confirme que l'IA a résolu le problème. */
  declarerResolue(id: number): Observable<Reclamation> {
    return this.http.post<Reclamation>(`${this.url}/${id}/resolution`, null);
  }

  /** GET /api/reclamations/{id}/historique : journal des changements de statut. */
  historique(id: number): Observable<HistoriqueStatut[]> {
    return this.http.get<HistoriqueStatut[]>(`${this.url}/${id}/historique`);
  }
}