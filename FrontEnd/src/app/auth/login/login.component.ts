import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { AuthService } from '../auth.service';
import { LoginRequest } from '../auth.model';
import { toApiError } from '../../core/utils/api-error.util';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent {

  Login_Fill = {
    email: '',
    motDePasse: ''
  };

  showPassword: boolean = false;
  isLoading: boolean = false;
  loginError: boolean = false;
  errorMessage: string = '';

  /**
   * Page d'arrivée selon le rôle. Ajoutez AGENT_SUPPORT et ADMIN ici
   * quand leurs écrans existeront (une route inexistante ferait échouer la navigation).
   */
  private readonly routesParRole: Record<string, string> = {
    CLIENT: '/reclamation-form',
  };
  private readonly routeParDefaut = '/reclamation-form';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    if (this.isLoading) { return; }

    this.loginError = false;
    this.errorMessage = '';

    const request: LoginRequest = {
      email: this.Login_Fill.email.trim(),
      motDePasse: this.Login_Fill.motDePasse
    };

    this.isLoading = true;

    this.authService.login(request).subscribe({
      next: (response) => {
        // Le token et les informations du compte sont déjà enregistrés dans la session par AuthService.
        this.isLoading = false;
        this.router.navigate([this.routesParRole[response.role] ?? this.routeParDefaut]);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.loginError = true;

        if (err.status === 401) {
          // Message volontairement identique que l'email existe ou non.
          this.errorMessage = 'Incorrect email or password.';
        } else if (err.status === 403) {
          this.errorMessage = 'This account is disabled. Please contact support.';
        } else {
          this.errorMessage = toApiError(err).message;
        }

        // On ne garde jamais un mot de passe refusé dans le formulaire.
        this.Login_Fill.motDePasse = '';
      }
    });
  }
}