import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_URL } from '../core/config/api.config';
import { AuthResponse, LoginRequest, RegisterRequest } from '../auth/auth.model';
import { ApiError } from '../core/models/api-error.model';

@Injectable({ providedIn: 'root' })
export class ClientService {

  private readonly authUrl = `${API_URL}/auth`;

  constructor(private http: HttpClient) {}

  /**
   * POST /api/auth/register
   * Succès : 201 Created + AuthResponse (token inclus).
   * Erreurs : 400 (champs invalides), 409 (email ou téléphone déjà utilisé).
   * HttpClient n'émet que pour un statut 2xx : un 201 arrive donc dans next().
   */
  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.authUrl}/register`, request);
  }

  /**
   * POST /api/auth/login
   * Succès : 200 + AuthResponse. Erreur : 401 (email ou mot de passe incorrect).
   */
  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.authUrl}/login`, request);
  }
}

/**
 * Transforme une erreur HTTP en message affichable.
 * 400 -> le serveur renvoie { champs: { email: "...", motDePasse: "..." } }
 * 409 -> { message: "Un compte existe deja avec cet email." }
 */
export function toApiError(err: HttpErrorResponse): ApiError {
  switch (err.status) {
    case 0:
      return { message: 'Server unreachable. Check that the backend is running (and CORS).', fieldErrors: {} };
    case 400:
      return { message: 'Please correct the highlighted fields.', fieldErrors: err.error?.champs ?? {} };
    case 401:
      return { message: err.error?.message ?? 'Incorrect email or password.', fieldErrors: {} };
    case 409:
      return { message: err.error?.message ?? 'This account already exists.', fieldErrors: {} };
    default:
      return { message: 'Something went wrong. Please try again.', fieldErrors: {} };
  }
}