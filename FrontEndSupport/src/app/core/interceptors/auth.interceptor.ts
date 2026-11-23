import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

import { API_URL } from '../config/api.config';
import { SessionService } from '../services/session.service';

/**
 * Ajoute automatiquement "Authorization: Bearer <token>" à chaque requête vers l'API :
 * aucun service n'a plus à s'en occuper.
 *  - le token n'est jamais envoyé à un autre domaine ;
 *  - il n'est pas envoyé aux routes d'authentification (/auth/login, /auth/register).
 * Si l'API répond 403 alors que le token a expiré, la session est vidée.
 * (Un 403 avec un token valide = rôle insuffisant : la session est conservée.)
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const session = inject(SessionService);
  const token = session.getToken();

  const versApi = req.url.startsWith(API_URL);
  const routeAuth = req.url.startsWith(`${API_URL}/auth/`);
  const requete = (token && versApi && !routeAuth)
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(requete).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 403 && token && !session.isLoggedIn()) {
        session.clear();
      }
      return throwError(() => err);
    })
  );
};