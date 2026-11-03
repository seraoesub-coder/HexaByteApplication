import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_URL } from '../core/config/api.config';
import { ClientProfile, UpdateProfileRequest } from './client.model';

/** Profil du client connecté. Correspond à ClientController côté backend. */
@Injectable({ providedIn: 'root' })
export class ClientService {

  private readonly url = `${API_URL}/clients/me`;

  constructor(private http: HttpClient) {}

  /** GET /api/clients/me */
  getProfile(): Observable<ClientProfile> {
    return this.http.get<ClientProfile>(this.url);
  }

  /** PATCH /api/clients/me : seuls les champs envoyés sont modifiés. */
  updateProfile(request: UpdateProfileRequest): Observable<ClientProfile> {
    return this.http.patch<ClientProfile>(this.url, request);
  }
}