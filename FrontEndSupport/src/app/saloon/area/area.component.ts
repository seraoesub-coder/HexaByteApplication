import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import { SessionService } from '../../core/services/session.service'; // Ajustez le chemin


import { LoginRequest } from 'src/app/auth/auth.model';
import { HttpErrorResponse } from '@angular/common/http';
import { toApiError } from 'src/app/core/utils/api-error.util';
import { AuthService } from 'src/app/auth/auth.service';

export interface Zone {
  id: number;
  name: string;
  status: 'available' | 'occupied' | 'maintenance' | 'closed';
  statusLabel: string;
  pseudo?: string;
  avatarUrl?: string;
}

@Component({
  selector: 'app-area',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './area.component.html',
  styleUrls: ['./area.component.css']
})

export class AreaComponent {

// Injection du Router d'Angular
  private router = inject(Router);

  // Dans votre composant :
  private session = inject(SessionService);


    // Dans votre composant :
 private authService = inject(AuthService); 

// Filtre sélectionné
  selectedFilter: 'all' | 'available' | 'occupied' | 'maintenance' | 'closed' = 'all';

  // Gestion de la modale
  isModalOpen: boolean = false;
  selectedZone: Zone | null = null;
  
  // Champs du formulaire
  newPseudo: string = '';
  enteredPassword: string = '';
  

    Login_Fill = {
    email: 'agent.support@hexabyte.tn',
    motDePasse: 'Support2026'
  };

  loginError: boolean = false;

  isLoading: boolean = false;

  // Mot de passe fixe imposé
  readonly REQUIRED_PASSWORD: string = 'hexabyte';
  errorMessage: string = '';

  private readonly routesParRole: Record<string, string> = {
    AGENT_SUPPORT: '/admin/area',
  };

  private readonly routeParDefaut = '/admin/area';

  // Liste des 12 zones
  zones: Zone[] = [
    { id: 1, name: 'Zone 1', status: 'available', statusLabel: 'Disponible' },
    { id: 2, name: 'Zone 2', status: 'occupied', statusLabel: 'Occupé', pseudo: 'Agent Karim' },
    { id: 3, name: 'Zone 3', status: 'maintenance', statusLabel: 'En maintenance' },
    { id: 4, name: 'Zone 4', status: 'available', statusLabel: 'Disponible' },
    { id: 5, name: 'Zone 5', status: 'closed', statusLabel: 'Fermé (Admin)' },
    { id: 6, name: 'Zone 6', status: 'occupied', statusLabel: 'Occupé', pseudo: 'Tech Sonia' },
    { id: 7, name: 'Zone 7', status: 'available', statusLabel: 'Disponible' },
    { id: 8, name: 'Zone 8', status: 'maintenance', statusLabel: 'En maintenance' },
    { id: 9, name: 'Zone 9', status: 'available', statusLabel: 'Disponible' },
    { id: 10, name: 'Zone 10', status: 'occupied', statusLabel: 'Occupé', pseudo: 'Admin Mehdi' },
    { id: 11, name: 'Zone 11', status: 'closed', statusLabel: 'Fermé (Admin)' },
    { id: 12, name: 'Zone 12', status: 'available', statusLabel: 'Disponible' }
  ];

  get filteredZones(): Zone[] {
    if (this.selectedFilter === 'all') return this.zones;
    return this.zones.filter(zone => zone.status === this.selectedFilter);
  }

  
  // Clic sur une zone
  onZoneClick(zone: Zone): void {
    this.selectedZone = zone;
    this.newPseudo = zone.pseudo || '';
    this.enteredPassword = '';
    this.errorMessage = '';
    this.isModalOpen = true;
  
    console.log('Token actuel dans la session :', this.session.getToken());
    console.log('Utilisateur connecté :', this.session.getUser());
   
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedZone = null;
    this.errorMessage = '';
  }

  // Validation du mot de passe 'hexabyte', sauvegarde et redirection vers /admin/zone
  saveZoneProfile(): void {
    if (this.enteredPassword !== this.REQUIRED_PASSWORD) {
      this.errorMessage = 'Mot de passe incorrect (Indice: hexabyte)';
      return;
    }

    if (this.selectedZone) {
      this.selectedZone.pseudo = this.newPseudo;
      
      if (this.newPseudo.trim() !== '') {
        this.selectedZone.status = 'occupied';
        this.selectedZone.statusLabel = 'Occupé';
      }
    }

    

    this.closeModal();

    // 🚀 Redirection vers /admin/status après confirmation
    this.router.navigate(['/admin/zone']);
  }

    authentification() {
    // Évite les clics multiples pendant la requête
    if (this.isLoading) return;

    this.isLoading = true;
    this.loginError = false;
    this.errorMessage = '';

    const request: LoginRequest = {
      email: this.Login_Fill.email.trim(),
      motDePasse: this.Login_Fill.motDePasse
    };

    console.log('Login request:', request);

    
    this.authService.login(request).subscribe({
      next: (response) => {
        this.isLoading = false;
        // Le token est déjà enregistré par AuthService. On redirige selon le rôle.
        const targetRoute = this.routesParRole[response.role] ?? this.routeParDefaut;
        this.router.navigate([targetRoute]);
      },
      
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.loginError = true;

        if (err.status === 401) {
          this.errorMessage = 'Incorrect email or password.';
        } else if (err.status === 403) {
          this.errorMessage = 'This account is disabled. Please contact support.';
        } else {
          this.errorMessage = toApiError(err).message;
        }

        // On nettoie le mot de passe en cas d'échec
        this.Login_Fill.motDePasse = '';
      }
    });
  }

}
