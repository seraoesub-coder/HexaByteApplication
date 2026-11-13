import { Component } from '@angular/core';
import { DashboardService } from "../dashboard.service";
import { Router } from '@angular/router';
import { AuthService } from 'src/app/auth/auth.service';
import { LoginRequest } from 'src/app/auth/auth.model';
import { HttpErrorResponse } from '@angular/common/http';
import { toApiError } from 'src/app/core/utils/api-error.util';

@Component({
  selector: 'top-bar',
  templateUrl: './top-bar.component.html',
})
export class TopBarComponent {

  constructor(
    private dashboard: DashboardService, 
    private authService: AuthService, 
    private router: Router
  ) {}

  Login_Fill = {
    email: 'agent.support@hexabyte.tn',
    motDePasse: 'Support2026'
  };

  loginError: boolean = false;
  errorMessage: string = '';
  isLoading: boolean = false;

  private readonly routesParRole: Record<string, string> = {
    AGENT_SUPPORT: '/admin/area',
  };

  private readonly routeParDefaut = '/admin/area';

  openSidebar() {
    this.dashboard.openSidebar();
  }

  // authentification() {
  //   // Évite les clics multiples pendant la requête
  //   if (this.isLoading) return;

  //   this.isLoading = true;
  //   this.loginError = false;
  //   this.errorMessage = '';

  //   const request: LoginRequest = {
  //     email: this.Login_Fill.email.trim(),
  //     motDePasse: this.Login_Fill.motDePasse
  //   };

  //   console.log('Login request:', request);

  //   this.authService.login(request).subscribe({
  //     next: (response) => {
  //       this.isLoading = false;
  //       // Le token est déjà enregistré par AuthService. On redirige selon le rôle.
  //       const targetRoute = this.routesParRole[response.role] ?? this.routeParDefaut;
  //       this.router.navigate([targetRoute]);
  //     },
      
  //     error: (err: HttpErrorResponse) => {
  //       this.isLoading = false;
  //       this.loginError = true;

  //       if (err.status === 401) {
  //         this.errorMessage = 'Incorrect email or password.';
  //       } else if (err.status === 403) {
  //         this.errorMessage = 'This account is disabled. Please contact support.';
  //       } else {
  //         this.errorMessage = toApiError(err).message;
  //       }

  //       // On nettoie le mot de passe en cas d'échec
  //       this.Login_Fill.motDePasse = '';
  //     }
  //   });
  // }
}