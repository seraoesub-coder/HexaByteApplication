import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

import { API_URL } from '../core/config/api.config';
import { SessionService } from '../core/services/session.service';
import { AuthResponse, LoginRequest, RegisterRequest } from './auth.model';

/** Inscription, connexion, déconnexion. Correspond à AuthController côté backend. */
@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly url = `${API_URL}/auth`;

  constructor(private http: HttpClient, private session: SessionService) {}

  /** POST /api/auth/register -> 201 Created. Le client est connecté dès son inscription. */
  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.url}/register`, request)
      .pipe(tap(res => this.session.save(res)));
  }

  /** POST /api/auth/login -> 200. Erreur : 401 (email ou mot de passe incorrect). */
  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.url}/login`, request)
      .pipe(tap(res => this.session.save(res)));
  }

  /** Efface le token du navigateur (le serveur n'a pas de session à fermer). */
  logout(): void {
    this.session.clear();
  }
}