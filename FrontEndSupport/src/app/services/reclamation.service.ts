import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_URL } from '../core/config/api.config';
import { Page } from '../core/models/page.model';
import { CreateReclamationRequest, HistoriqueStatut, Reclamation, CarnetEntree} from './reclamation.model';

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

  // Récupérer la liste complète des réclamations
  getReclamations(): Observable<Reclamation[]> {
    return this.http.get<Reclamation[]>(this.url);
  }

  updatePriorite(reclamationId: number, nouvellePriorite: string): Observable<any> {
    // Adapte l'URL selon l'endpoint de ton API Spring Boot (ex: /api/reclamations/{id}/priorite ou /api/support/reclamations/{id})
    const url = `${this.url}/${reclamationId}/priorite`; 
    return this.http.patch(url, { priorite: nouvellePriorite });
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

// 1. Récupérer les messages d'une réclamation
  getMessages(reclamationId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/${reclamationId}/messages`);
  }

  repondreClient(reclamationId: number, contenuMessage: string): Observable<any> {
    const body = { contenu: contenuMessage };
    return this.http.post(`${this.url}/${reclamationId}/reponse`, body);
  }

  // reclamation.service.ts
  getRendezVous(page: number = 0, size: number = 20): Observable<any> {
    return this.http.get<any>(`http://localhost:8080/api/rendez-vous`);
  }

  ajouterNoteCarnet(reclamationId: number, contenu: string): Observable<CarnetEntree> {
    return this.http.post<CarnetEntree>(
      `http://localhost:8080/api/support/reclamations/${reclamationId}/carnet`,
      { contenu }
    );
  }

  getCarnetReclamation(reclamationId: number): Observable<CarnetEntree[]> {
    return this.http.get<CarnetEntree[]>(
      `http://localhost:8080/api/support/reclamations/${reclamationId}/carnet`
    );
  }

  cloturerReclamation(reclamationId: number, motif: string): Observable<any> {
  return this.http.post(
    `http://localhost:8080/api/support/reclamations/${reclamationId}/cloture`,
    { motif }
  );
}
  // Confirmer la réalisation d'un rendez-vous par son ID avec un motif / note optionnel
confirmerRealisationRendezVous(rendezVousId: number, contenu?: string): Observable<any> {
  const body = contenu?.trim() ? { contenu: contenu.trim() } : {};
  return this.http.post(
    `http://localhost:8080/api/rendez-vous/${rendezVousId}/realisation`,
    body
  );
}
}