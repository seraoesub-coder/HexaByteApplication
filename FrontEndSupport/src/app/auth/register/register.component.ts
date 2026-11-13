import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { AuthService } from '../auth.service';
import { RegisterRequest } from '../auth.model';
import { ApiError } from '../../core/models/api-error.model';
import { toApiError } from '../../core/utils/api-error.util';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  standalone: false,   // composant déclaré dans AppModule ; supprimez cette ligne si Angular <= 13
})
export class RegisterComponent {

  Client_Fill = {
    email: '',
    motDePasse: '',
    confirmPassword: '',      // vérifié dans le navigateur uniquement, jamais envoyé à l'API
    nom: '',
    prenom: '',
    telephone: '',
    adresseInstallation: '',
    gouvernorat: ''
  };

  /** Les 24 gouvernorats de Tunisie. */
  gouvernorats: string[] = [
    'Ariana', 'Béja', 'Ben Arous', 'Bizerte', 'Gabès', 'Gafsa', 'Jendouba', 'Kairouan',
    'Kasserine', 'Kébili', 'Le Kef', 'Mahdia', 'La Manouba', 'Médenine', 'Monastir', 'Nabeul',
    'Sfax', 'Sidi Bouzid', 'Siliana', 'Sousse', 'Tataouine', 'Tozeur', 'Tunis', 'Zaghouan'
  ];

  passwordStrength: string = '';
  registrationSuccess: boolean = false;
  registrationError: boolean = false;
  errorMessage: string = '';
  fieldErrors: Record<string, string> = {};
  isLoading: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  checkPasswordStrength(): void {
    const pwd = this.Client_Fill.motDePasse;
    if (!pwd) {
      this.passwordStrength = '';
      return;
    }

    let score = 0;
    if (pwd.length >= 8) score++;
    if (/[A-Z]/.test(pwd)) score++;
    if (/[a-z]/.test(pwd)) score++;
    if (/\d/.test(pwd)) score++;
    if (/[@$!%*?&]/.test(pwd)) score++;

    if (score <= 2) this.passwordStrength = 'Weak';
    else if (score <= 4) this.passwordStrength = 'Medium';
    else this.passwordStrength = 'Strong';
  }

  getPasswordStrengthClass(): string {
    switch (this.passwordStrength) {
      case 'Weak': return 'text-red-500 font-bold';
      case 'Medium': return 'text-yellow-500 font-bold';
      case 'Strong': return 'text-green-500 font-bold';
      default: return '';
    }
  }

  onSubmit(): void {
    this.registrationSuccess = false;
    this.registrationError = false;
    this.errorMessage = '';
    this.fieldErrors = {};

    if (this.Client_Fill.motDePasse !== this.Client_Fill.confirmPassword) {
      this.registrationError = true;
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    // Seuls les champs de l'API sont envoyés (pas de confirmPassword).
    const requestPayload: RegisterRequest = {
      email: this.Client_Fill.email.trim(),
      motDePasse: this.Client_Fill.motDePasse,
      nom: this.Client_Fill.nom.trim(),
      prenom: this.Client_Fill.prenom.trim(),
      telephone: this.Client_Fill.telephone ? this.Client_Fill.telephone.trim() : undefined,
      adresseInstallation: this.Client_Fill.adresseInstallation ? this.Client_Fill.adresseInstallation.trim() : undefined,
      gouvernorat: this.Client_Fill.gouvernorat || undefined
    };

    this.isLoading = true;

    this.authService.register(requestPayload).subscribe({
      next: () => {
        // Le token et les informations du compte sont déjà enregistrés dans la session
        // par AuthService : plus aucun localStorage.setItem ici.
        this.isLoading = false;
        this.registrationSuccess = true;

        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 1200);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.registrationError = true;

        const apiError: ApiError = toApiError(err);
        this.errorMessage = apiError.message;
        this.fieldErrors = apiError.fieldErrors;
      }
    });
  }
}