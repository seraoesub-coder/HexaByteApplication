import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { Reclamation } from '../../reclamation/reclamation.model';
import { ReclamationService } from '../../reclamation/reclamation.service';
import { SessionService } from '../../core/services/session.service';
import { toApiError } from '../../core/utils/api-error.util';
import { RendezVous } from '../rendez-vous.model';
import { RendezVousService } from '../rendez-vous.service';

@Component({
  selector: 'app-rendez-vous-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './rendez-vous-list.component.html',
})
export class RendezVousListComponent implements OnInit {

  rendezVous: RendezVous[] = [];
  reclamations: Reclamation[] = [];        // pour rattacher le rendez-vous à une réclamation (facultatif)

  reclamationId: number | null = null;
  motif = '';

  loading = true;
  requesting = false;
  actionId: number | null = null;          // rendez-vous en cours de confirmation / annulation
  errorMessage = '';
  notice = '';

  readonly stars = [1, 2, 3, 4, 5];

  constructor(
    private rendezVousService: RendezVousService,
    private reclamationService: ReclamationService,
    public session: SessionService,
  ) {}

  ngOnInit(): void {
    if (!this.session.isLoggedIn()) {
      this.loading = false;          // le gabarit affiche l'invitation à se connecter
      return;
    }
    this.charger();
  }

  private charger(): void {
    this.loading = true;
    forkJoin({
      rendezVous: this.rendezVousService.lister(0, 50),
      reclamations: this.reclamationService.lister(0, 100),
    }).subscribe({
      next: ({ rendezVous, reclamations }) => {
        this.rendezVous = rendezVous.content;
        this.reclamations = reclamations.content.filter(r => r.statut !== 'CLOTUREE');
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = toApiError(err).message;
      },
    });
  }

  // ----------------------------- Règles d'affichage -----------------------------

  /** Une proposition dont la date est passée ne peut plus être confirmée. */
  expiree(r: RendezVous): boolean {
    return new Date(r.dateHeure).getTime() <= Date.now();
  }

  /** Le serveur n'accepte qu'une seule proposition en attente à la fois. */
  get propositionEnAttente(): boolean {
    return this.rendezVous.some(r => r.statut === 'PROPOSE' && !this.expiree(r));
  }

  peutConfirmer(r: RendezVous): boolean { return r.statut === 'PROPOSE' && !this.expiree(r); }
  peutAnnuler(r: RendezVous): boolean { return r.statut === 'PROPOSE' || r.statut === 'CONFIRME'; }

  /** Le serveur demande l'adresse d'installation : on propose d'aller la renseigner. */
  get adresseManquante(): boolean {
    return this.errorMessage.toLowerCase().includes('adresse');
  }

  statutLibelle(r: RendezVous): string {
    if (r.statut === 'PROPOSE' && this.expiree(r)) { return 'Proposal expired'; }
    switch (r.statut) {
      case 'PROPOSE': return 'Date proposed';
      case 'CONFIRME': return 'Confirmed';
      case 'ANNULE': return 'Cancelled';
      case 'REALISE': return 'Done';
    }
  }

  statutClasse(r: RendezVous): string {
    if (r.statut === 'PROPOSE' && this.expiree(r)) { return 'bg-gray-200 text-gray-700'; }
    switch (r.statut) {
      case 'PROPOSE': return 'bg-yellow-100 text-yellow-800';
      case 'CONFIRME': return 'bg-green-100 text-green-800';
      case 'REALISE': return 'bg-blue-100 text-blue-800';
      default: return 'bg-gray-200 text-gray-700';
    }
  }

  // ----------------------------- Actions -----------------------------

  /** Le système propose une date ; aucun technicien n'est encore affecté. */
  demander(): void {
    if (this.requesting || this.propositionEnAttente) { return; }
    this.requesting = true;
    this.errorMessage = '';
    this.notice = '';

    this.rendezVousService.demander({
      reclamationId: this.reclamationId ?? undefined,
      motif: this.motif.trim() || undefined,
    }).subscribe({
      next: (rdv) => {
        this.requesting = false;
        this.rendezVous = [rdv, ...this.rendezVous];
        this.reclamationId = null;
        this.motif = '';
        this.notice = 'We propose the date below. Please confirm it to get your technician.';
      },
      error: (err: HttpErrorResponse) => {
        this.requesting = false;
        this.errorMessage = toApiError(err).message;
      },
    });
  }

  /** Confirmer la date : un technicien disponible est alors affecté et ses coordonnées s'affichent. */
  confirmer(r: RendezVous): void {
    this.lancer(r, this.rendezVousService.confirmer(r.id),
      'Appointment confirmed. Your technician is shown below.');
  }

  annuler(r: RendezVous): void {
    this.lancer(r, this.rendezVousService.annuler(r.id), 'Appointment cancelled.');
  }

  private lancer(r: RendezVous, appel: ReturnType<RendezVousService['confirmer']>, message: string): void {
    if (this.actionId !== null) { return; }
    this.actionId = r.id;
    this.errorMessage = '';
    this.notice = '';
    appel.subscribe({
      next: (maj) => {
        this.actionId = null;
        this.rendezVous = this.rendezVous.map(x => x.id === maj.id ? maj : x);
        this.notice = message;
      },
      error: (err: HttpErrorResponse) => {
        this.actionId = null;
        this.errorMessage = toApiError(err).message;   // ex. annulation impossible à moins de 24 h
      },
    });
  }
}