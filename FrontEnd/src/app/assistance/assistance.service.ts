import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_URL } from '../core/config/api.config';
import { Echange, Evaluation, EvaluationRequest, Message } from './assistance.model';

/**
 * Conversation d'une réclamation avec l'agent IA, et notation des réponses.
 * Correspond à ConversationController côté backend.
 * Erreur 503 = l'IA est momentanément indisponible (rien n'est enregistré, on peut réessayer).
 */
@Injectable({ providedIn: 'root' })
export class AssistanceService {

  private readonly url = `${API_URL}/reclamations`;

  constructor(private http: HttpClient) {}

  /** POST /api/reclamations/{id}/assistance -> 201. La description devient le premier message. Une seule fois. */
  demarrer(reclamationId: number): Observable<Echange> {
    return this.http.post<Echange>(`${this.url}/${reclamationId}/assistance`, null);
  }

  /** POST /api/reclamations/{id}/messages -> 200. */
  envoyer(reclamationId: number, contenu: string): Observable<Echange> {
    return this.http.post<Echange>(`${this.url}/${reclamationId}/messages`, { contenu });
  }

  /** GET /api/reclamations/{id}/messages : historique complet (client, IA et support). */
  historique(reclamationId: number): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.url}/${reclamationId}/messages`);
  }

  /** POST /api/reclamations/{id}/messages/{messageId}/evaluation -> 201. Une seule note par réponse de l'IA. */
  evaluer(reclamationId: number, messageId: number, request: EvaluationRequest): Observable<Evaluation> {
    return this.http.post<Evaluation>(`${this.url}/${reclamationId}/messages/${messageId}/evaluation`, request);
  }
}