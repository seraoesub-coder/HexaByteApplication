import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_URL } from '../core/config/api.config';
import { Page } from '../core/models/page.model';
import { CreateRendezVousRequest, RendezVous } from './rendez-vous.model';

/** Rendez-vous d'intervention du client. Correspond à RendezVousController côté backend. */
@Injectable({ providedIn: 'root' })
export class RendezVousService {

  private readonly url = `${API_URL}/rendez-vous`;

  constructor(private http: HttpClient) {}

  /** POST /api/rendez-vous -> 201. Le système propose une date ; aucun technicien n'est encore affecté. */
  demander(request?: CreateRendezVousRequest): Observable<RendezVous> {
    return this.http.post<RendezVous>(this.url, request ?? null);
  }

  /** POST /api/rendez-vous/{id}/confirmation -> 200. Un technicien est alors tiré au hasard. */
  confirmer(id: number): Observable<RendezVous> {
    return this.http.post<RendezVous>(`${this.url}/${id}/confirmation`, null);
  }

  /** POST /api/rendez-vous/{id}/annulation -> 200 (impossible moins de 24 h avant un rendez-vous confirmé). */
  annuler(id: number): Observable<RendezVous> {
    return this.http.post<RendezVous>(`${this.url}/${id}/annulation`, null);
  }

  /** GET /api/rendez-vous?page=0&size=10 */
  lister(page = 0, size = 10): Observable<Page<RendezVous>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<RendezVous>>(this.url, { params });
  }

  /** GET /api/rendez-vous/{id} */
  obtenir(id: number): Observable<RendezVous> {
    return this.http.get<RendezVous>(`${this.url}/${id}`);
  }
}