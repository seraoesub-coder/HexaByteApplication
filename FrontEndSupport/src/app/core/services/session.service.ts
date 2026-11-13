import { Injectable } from '@angular/core';
import { AuthResponse } from '../../auth/auth.model';
import { SessionUser } from '../models/session-user';

/**
 * Garde la session du compte connecté : le token JWT et quelques informations d'affichage.
 * sessionStorage : survit à un rechargement de page, disparaît à la fermeture de l'onglet
 * (et n'est pas partagé entre deux onglets).
 * Limite connue : lisible par tout script de la page (XSS). Pour un déploiement réel,
 * préférez un cookie HttpOnly géré par le serveur.
 */
@Injectable({ providedIn: 'root' })
export class SessionService {

  private readonly TOKEN_KEY = 'hexa_token';
  private readonly USER_KEY = 'hexa_user';

  private token: string | null = null;
  private user: SessionUser | null = null;

  constructor() {
    try {
      this.token = sessionStorage.getItem(this.TOKEN_KEY);
      const raw = sessionStorage.getItem(this.USER_KEY);
      this.user = raw ? JSON.parse(raw) : null;
    } catch { /* stockage indisponible ou contenu illisible */ }
  }

  /** Appelé par AuthService après une inscription ou une connexion réussie. */
  save(response: AuthResponse): void {
    this.token = response.accessToken;
    this.user = {
      userId: response.userId,
      email: response.email,
      role: response.role,
      numeroClient: response.numeroClient,
      nomComplet: response.nomComplet,
    };
    try {
      sessionStorage.setItem(this.TOKEN_KEY, this.token);
      sessionStorage.setItem(this.USER_KEY, JSON.stringify(this.user));
    } catch { /* ignoré */ }
  }

  getToken(): string | null {
    return this.token;
  }

  /** Email, rôle et numéro client du compte connecté (remplace user_email, user_role, client_number). */
  getUser(): SessionUser | null {
    return this.user;
  }

  /** Connecté = un token existe ET n'a pas expiré (durée de vie : 1 heure). */
  isLoggedIn(): boolean {
    const payload = this.token ? this.payload(this.token) : null;
    return payload !== null && typeof payload.exp === 'number' && payload.exp * 1000 > Date.now();
  }

  /** Rôle lu dans le token (source fiable) : "CLIENT", "AGENT_SUPPORT" ou "ADMIN". */
  getRole(): string | null {
    const payload = this.token ? this.payload(this.token) : null;
    return payload?.role ?? null;
  }

  clear(): void {
    this.token = null;
    this.user = null;
    try {
      sessionStorage.removeItem(this.TOKEN_KEY);
      sessionStorage.removeItem(this.USER_KEY);
    } catch { /* ignoré */ }
  }

  /** Le token n'est pas chiffré : son contenu se lit, c'est la signature qui le protège. */
  private payload(token: string): { exp?: number; role?: string } | null {
    try {
      const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(base64));
    } catch {
      return null;
    }
  }
}