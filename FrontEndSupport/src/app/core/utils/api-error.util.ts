import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '../models/api-error.model';

/**
 * Transforme une erreur HTTP du backend en message affichable.
 * 400 -> { champs: { email: "...", motDePasse: "..." } }   409 -> { message: "..." }
 * 403 -> pas de token, token expiré, ou rôle insuffisant (dans cette API un 403 remplace le 401).
 * 503 -> l'assistant IA est momentanément indisponible.
 */
export function toApiError(err: HttpErrorResponse): ApiError {
  switch (err.status) {
    case 0:
      return { message: 'Server unreachable. Check that the backend is running (and CORS).', fieldErrors: {} };
    case 400:
      return { message: 'Please correct the highlighted fields.', fieldErrors: err.error?.champs ?? {} };
    case 401:
      return { message: err.error?.message ?? 'Incorrect email or password.', fieldErrors: {} };
    case 403:
      return { message: 'Access denied. Your session may have expired: please log in again.', fieldErrors: {} };
    case 404:
      return { message: err.error?.message ?? 'Not found.', fieldErrors: {} };
    case 409:
      return { message: err.error?.message ?? 'This action is not possible in the current state.', fieldErrors: {} };
    case 503:
      return { message: 'The assistant is temporarily unavailable. Please try again in a moment.', fieldErrors: {} };
    default:
      return { message: 'Something went wrong. Please try again.', fieldErrors: {} };
  }
}