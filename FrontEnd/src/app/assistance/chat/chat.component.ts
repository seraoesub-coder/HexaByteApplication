import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { AssistanceService } from '../assistance.service';
import { Echange, Message } from '../assistance.model';
import { Reclamation } from '../../reclamation/reclamation.model';
import { ReclamationService } from '../../reclamation/reclamation.service';
import { SessionService } from '../../core/services/session.service';
import { toApiError } from '../../core/utils/api-error.util';

type Statut = Reclamation['statut'];

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './chat.component.html',
})
export class ChatComponent implements OnInit {

  @ViewChild('scroller') private scroller?: ElementRef<HTMLElement>;

  reclamationId = 0;
  reclamation: Reclamation | null = null;
  messages: Message[] = [];

  draft = '';                          // texte en cours de saisie (conservé si l'envoi échoue)
  pendingMessage: string | null = null; // message envoyé, en attente de la réponse de l'IA
  loading = true;
  aiBusy = false;                      // l'IA est en train de répondre
  actionBusy = false;                  // résolution / escalade en cours
  ratingId: number | null = null;      // message en cours de notation
  errorMessage = '';
  notice = '';

  readonly stars = [1, 2, 3, 4, 5];

  constructor(
    private route: ActivatedRoute,
    private reclamationService: ReclamationService,
    private assistanceService: AssistanceService,
    public session: SessionService,
  ) {}

  ngOnInit(): void {
    this.reclamationId = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isInteger(this.reclamationId) || this.reclamationId <= 0) {
      this.loading = false;
      this.errorMessage = 'Invalid complaint.';
      return;
    }
    if (!this.session.isLoggedIn()) {
      this.loading = false;          // le gabarit affiche l'invitation à se connecter
      return;
    }
    this.charger();
  }

  // ----------------------------- État de la réclamation -----------------------------

  /** Le client peut écrire tant que la réclamation est suivie par l'assistant. */
  get peutEcrire(): boolean { return this.reclamation?.statut === 'EN_COURS_IA'; }
  get peutDeclarerResolue(): boolean { return this.reclamation?.statut === 'EN_COURS_IA'; }
  get peutEscalader(): boolean {
    const s = this.reclamation?.statut;
    return s === 'OUVERTE' || s === 'EN_COURS_IA' || s === 'RESOLUE_PAR_IA';
  }
  get peutDemarrer(): boolean {
    return this.reclamation?.statut === 'OUVERTE' && this.messages.length === 0 && !this.aiBusy;
  }

  statutLibelle(): string {
    switch (this.reclamation?.statut) {
      case 'OUVERTE': return 'Open';
      case 'EN_COURS_IA': return 'In progress with the assistant';
      case 'RESOLUE_PAR_IA': return 'Resolved';
      case 'ESCALADEE_SUPPORT': return 'Transferred to a human advisor';
      case 'CLOTUREE': return 'Closed';
      default: return '';
    }
  }

  statutClasse(): string {
    switch (this.reclamation?.statut) {
      case 'EN_COURS_IA': return 'bg-blue-100 text-blue-800';
      case 'RESOLUE_PAR_IA': return 'bg-green-100 text-green-800';
      case 'ESCALADEE_SUPPORT': return 'bg-orange-100 text-orange-800';
      default: return 'bg-gray-200 text-gray-800';
    }
  }

  roleLabel(role: Message['role']): string {
    return role === 'USER' ? 'You' : role === 'ASSISTANT' ? 'Assistant' : 'Support advisor';
  }

  bubbleClasse(role: Message['role']): string {
    switch (role) {
      case 'USER': return 'bg-blue-600 text-white';
      case 'SUPPORT': return 'bg-green-100 text-green-900';
      default: return 'bg-gray-200 text-gray-900 dark:bg-gray-700 dark:text-white';
    }
  }

  // ----------------------------- Chargement -----------------------------

  private charger(): void {
    this.loading = true;
    forkJoin({
      reclamation: this.reclamationService.obtenir(this.reclamationId),
      messages: this.assistanceService.historique(this.reclamationId),
    }).subscribe({
      next: ({ reclamation, messages }) => {
        this.reclamation = reclamation;
        this.messages = messages;
        this.loading = false;
        this.scrollVersLeBas();
        // Première ouverture : l'assistant démarre tout seul à partir de la description.
        if (messages.length === 0 && reclamation.statut === 'OUVERTE') {
          this.demarrer();
        }
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = toApiError(err).message;
      },
    });
  }

  private rechargerMessages(): void {
    this.assistanceService.historique(this.reclamationId).subscribe({
      next: (messages) => { this.messages = messages; this.scrollVersLeBas(); },
    });
  }

  // ----------------------------- Conversation -----------------------------

  /** Lance l'assistant : la description de la réclamation devient le premier message. */
  demarrer(): void {
    if (this.aiBusy) { return; }
    this.aiBusy = true;
    this.errorMessage = '';

    this.assistanceService.demarrer(this.reclamationId).subscribe({
      next: (echange) => { this.aiBusy = false; this.ajouterEchange(echange); },
      error: (err: HttpErrorResponse) => {
        this.aiBusy = false;
        if (err.status === 409) {
          this.rechargerMessages();      // déjà démarré (autre onglet) : on affiche la conversation
        } else {
          this.errorMessage = toApiError(err).message;   // ex. 503 : on peut réessayer
        }
      },
    });
  }

  envoyer(): void {
    const texte = this.draft.trim();
    if (!texte || this.aiBusy || !this.peutEcrire) { return; }

    this.aiBusy = true;
    this.errorMessage = '';
    this.pendingMessage = texte;

    this.assistanceService.envoyer(this.reclamationId, texte).subscribe({
      next: (echange) => {
        this.aiBusy = false;
        this.pendingMessage = null;
        this.draft = '';
        this.ajouterEchange(echange);
      },
      error: (err: HttpErrorResponse) => {
        // Rien n'a été enregistré côté serveur : le texte reste dans la zone de saisie pour réessayer.
        this.aiBusy = false;
        this.pendingMessage = null;
        this.errorMessage = toApiError(err).message;
      },
    });
  }

  /** Entrée envoie le message ; Maj+Entrée passe à la ligne. */
  envoyerParEntree(event: Event): void {
    const clavier = event as KeyboardEvent;
    if (clavier.shiftKey) { return; }
    clavier.preventDefault();
    this.envoyer();
  }

  private ajouterEchange(echange: Echange): void {
    this.messages = [...this.messages, echange.question, echange.reponse];
    if (this.reclamation) {
      this.reclamation.statut = echange.statutReclamation as Statut;
    }
    this.scrollVersLeBas();
  }

  // ----------------------------- Note, résolution, escalade -----------------------------

  evaluer(message: Message, note: number): void {
    if (message.note !== null || this.ratingId !== null) { return; }
    this.ratingId = message.id;
    this.errorMessage = '';

    this.assistanceService.evaluer(this.reclamationId, message.id, { note }).subscribe({
      next: (evaluation) => {
        this.ratingId = null;
        message.note = evaluation.note;
        if (this.reclamation) { this.reclamation.statut = evaluation.statutReclamation as Statut; }
        if (evaluation.statutReclamation === 'ESCALADEE_SUPPORT') {
          this.notice = 'Sorry this answer did not help. A human advisor will take over your complaint.';
        }
      },
      error: (err: HttpErrorResponse) => {
        this.ratingId = null;
        this.errorMessage = toApiError(err).message;
      },
    });
  }

  declarerResolue(): void {
    this.lancerAction(
      this.reclamationService.declarerResolue(this.reclamationId),
      'Great, we are glad it is solved! Your complaint is marked as resolved.');
  }

  escalader(): void {
    this.lancerAction(
      this.reclamationService.escalader(this.reclamationId),
      'Your request has been sent. A human advisor will take over your complaint.');
  }

  private lancerAction(appel: ReturnType<ReclamationService['escalader']>, message: string): void {
    if (this.actionBusy) { return; }
    this.actionBusy = true;
    this.errorMessage = '';
    appel.subscribe({
      next: (reclamation) => { this.actionBusy = false; this.reclamation = reclamation; this.notice = message; },
      error: (err: HttpErrorResponse) => { this.actionBusy = false; this.errorMessage = toApiError(err).message; },
    });
  }

  // ----------------------------- Confort -----------------------------

  private scrollVersLeBas(): void {
    setTimeout(() => {
      const el = this.scroller?.nativeElement;
      if (el) { el.scrollTop = el.scrollHeight; }
    });
  }
}