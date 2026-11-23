import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ReclamationService } from '../../services/reclamation.service';
import { CarnetEntree, Reclamation, SupportReclamationResponse } from '../../services/reclamation.model';

export interface NotificationItem {
  id: number;
  message: string;
  time: string;
  unread: boolean;
}

@Component({
  selector: 'app-zone',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './zone.component.html',
  styleUrls: ['./zone.component.css']
})

export class ZoneComponent implements OnInit{

  agentName: string = 'Karim Mansour';
  agentRole: string = 'Agent Support Senior';

  currentTimeFormatted: string = '';
  private clockInterval: any;

  isReclamationModalOpen: boolean = false;
  selectedReclamationDetails: any | null = null;

  showNotifications: boolean = false;
  notifications: NotificationItem[] = [
    { id: 1, message: 'Nouvelle réclamation urgente (Fibre Optique)', time: 'Il y a 5 min', unread: true },
    { id: 2, message: 'Technicien assigné à la Zone #04', time: 'Il y a 20 min', unread: true }
  ];

  searchTerm: string = '';
  selectedStatusFilter: string = 'TOUS';

  loading: boolean = false;
  errorMessage: string = '';

  isClientModalOpen: boolean = false;
  selectedClient: any | null = null; 
  selectedReclamationRef: string = '';

  messagesConversation: any[] = [];
  reponseClientTexte: string = '';
  carnetEntrees: CarnetEntree[] = [];
  chargementCarnet = false;
  isConversationOpen: boolean = false; // Pour ouvrir la grande vue de discussion

  // ➔ On fusionne en une seule source de vérité (reclamations)
  reclamations: SupportReclamationResponse[] = [];
  isLoading = true;


  // Variables d'état pour la modale enrichie
  messagesList: any[] = [];
  reponseMessage: string = '';
  carnetNote: string = '';
  isClotureConfirmed: boolean = false;
  
  rendezVousList: any[] = [];
  selectedRendezVousId: number | null = null;
  rendezVousRealise: boolean = false;
  carnetSupportNote = ''
  
  motifRealisation = '';

  motifCloture = '';

  constructor(private reclamationService: ReclamationService) {}

  ngOnInit(): void {
    this.loadReclamations();
    this.startClock();
    this.chargerRendezVous();
  }

  ngOnDestroy(): void {
    if (this.clockInterval) {
      clearInterval(this.clockInterval);
    }
  }

 loadReclamations(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.reclamationService.getReclamations().subscribe({
      next: (data: any) => {
        // Sécurité : si l'API renvoie un objet paginé de type Spring Data { content: [...] } 
        // ou directement un tableau, on gère les deux cas :
        if (Array.isArray(data)) {
          this.reclamations = data;
        } else if (data && Array.isArray(data.content)) {
          this.reclamations = data.content;
        } else {
          this.reclamations = [];
        }

        this.isLoading = false;
        console.log('Réclamations chargées :', this.reclamations);
      },
      error: (err) => {
        console.error('Erreur lors du chargement des réclamations :', err);
        this.errorMessage = 'Impossible de charger les réclamations.';
        this.isLoading = false;
        this.reclamations = []; // On s'assure de garder un tableau vide en cas d'erreur
      }
    });
  }

  openClientDetails(client: any, reference: string): void {
    this.selectedClient = client;
    this.selectedReclamationRef = reference;
    this.isClientModalOpen = true;
  }

  closeClientModal(): void {
    this.isClientModalOpen = false;
    this.selectedClient = null;
  }

  private startClock(): void {
    this.updateClock();
    this.clockInterval = setInterval(() => this.updateClock(), 1000);
  }

  private updateClock(): void {
    const now = new Date();
    let hours = now.getHours();
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    const ampm = hours >= 12 ? 'PM' : 'AM';

    hours = hours % 12;
    hours = hours ? hours : 12;
    this.currentTimeFormatted = `${String(hours).padStart(2, '0')}:${minutes}:${seconds} ${ampm}`;
  }

 
  // Fermer la modale
  closeReclamationDetails(): void {
    this.isReclamationModalOpen = false;
    this.selectedReclamationDetails = null;
  }
  
  // Méthode pour changer la priorité via PATCH
  changerPriorite(reclamationId: number, nouvellePriorite: string): void {
    this.reclamationService.updatePriorite(reclamationId, nouvellePriorite).subscribe({
      next: (response) => {
        console.log('Priorité mise à jour avec succès', response);
        // Met à jour localement l'objet sélectionné pour un retour visuel immédiat
        if (this.selectedReclamationDetails && this.selectedReclamationDetails.reclamation) {
          this.selectedReclamationDetails.reclamation.priorite = nouvellePriorite;
        }
        // Optionnel : recharger la liste complète pour synchroniser
        this.loadReclamations();
      },
      error: (err) => {
        console.error('Erreur lors de la mise à jour de la priorité :', err);
        alert('Impossible de mettre à jour la priorité.');
      }
    });
  }

// Charger les messages et ouvrir la modale de détails
  openReclamationDetails(item: any): void {
    this.selectedReclamationDetails = item;
    this.isReclamationModalOpen = true;
    this.reponseClientTexte = '';
    this.isConversationOpen = false;

    const recId = item.reclamation?.id;
    if (recId) {
      this.chargerMessages(recId);
    }
  }

  // Requête GET /api/support/reclamations/{id}/messages
  chargerMessages(reclamationId: number): void {
    this.reclamationService.getMessages(reclamationId).subscribe({
      next: (messages) => {
        this.messagesConversation = messages || [];
      },
      error: (err) => {
        console.error('Erreur lors du chargement des messages :', err);
        this.messagesConversation = [];
      }
    });
  }

  envoyerReponseClient(reclamationId: number): void {
    if (!this.reponseClientTexte.trim()) return;

    this.reclamationService.repondreClient(reclamationId, this.reponseClientTexte).subscribe({
      next: () => {
        alert('Réponse envoyée avec succès au client !');
        this.reponseClientTexte = '';
        this.chargerMessages(reclamationId); // Actualiser la discussion
      },
      error: (err) => {
        console.error("Erreur lors de l'envoi de la réponse :", err);
      }
    });
  }

 // Requête GET /api/support/reclamations/{id}/carnet
chargerCarnet(reclamationId: number): void {
  this.chargementCarnet = true;
  this.reclamationService.getCarnetReclamation(reclamationId).subscribe({
    next: (entrees) => {
      this.carnetEntrees = Array.isArray(entrees) ? entrees : [];
      this.chargementCarnet = false;
    },
    error: (err) => {
      console.error('Erreur lors du chargement du carnet :', err);
      this.carnetEntrees = [];
      this.chargementCarnet = false;
    }
  });
}


  loadMessages(reclamationId: number): void {
    this.reclamationService.getMessages(reclamationId).subscribe({
      next: (messages) => {
        this.messagesList = messages;
      },
      error: (err) => {
        console.error('Erreur chargement messages :', err);
        this.messagesList = [];
      }
    });
  }

  


sauvegarderCarnet(reclamationId: number | undefined): void {
  const contenu = this.carnetNote?.trim();
  if (!reclamationId || !contenu) return;

  this.reclamationService.ajouterNoteCarnet(reclamationId, contenu).subscribe({
    next: () => {
      this.carnetNote = '';
      this.chargerCarnet(reclamationId);
    },
    error: (err) => {
      console.error('Erreur lors de l’enregistrement dans le carnet :', err);
    }
  });
}

// Envoyer une réponse au client
  envoyerReponse(reclamationId: number): void {
    if (!this.reponseMessage.trim()) return;

    this.reclamationService.repondreClient(reclamationId, this.reponseMessage).subscribe({
      next: () => {
        alert('Réponse envoyée avec succès au client !');
        this.reponseMessage = '';
        this.loadMessages(reclamationId); // Recharger la conversation
      },
      error: (err) => {
        console.error("Erreur lors de l'envoi de la réponse :", err);
      }
    });
  }

  
 // Remplace ou complète ton tableau statique de notifications par ce getter dynamique
  get supportNotifications(): any[] {
    if (!Array.isArray(this.reclamations)) return [];

    // On filtre uniquement les réclamations dont le statut est ESCALADEE_SUPPORT et non résolues
    return this.reclamations
      .filter(item => item.reclamation?.statut === 'ESCALADEE_SUPPORT')
      .map((item, index) => ({
        id: item.reclamation.id || index,
        message: `Réclamation ${item.reclamation.reference} escaladée (${item.client?.nom || 'Client'})`,
        time: 'Action requise',
        unread: true
      }));
  }

  // Met à jour le compteur des non lues basées sur ce filtre
  get unreadNotificationsCount(): number {
    return this.supportNotifications.filter(n => n.unread).length;
  }

  markAllAsRead(): void {
    this.notifications.forEach(n => n.unread = false);
  }

  get filteredItems(): SupportReclamationResponse[] {
      if (!Array.isArray(this.reclamations)) {
        return [];
      }

      return this.reclamations.filter((item: SupportReclamationResponse) => {
        const search = this.searchTerm ? this.searchTerm.toLowerCase() : '';

        // Récupération sécurisée depuis item.client et item.reclamation
        const nom = item.client?.nom || '';
        const prenom = item.client?.prenom || '';
        const numClient = item.client?.numeroClient || '';
        
        const categorieCode = item.reclamation?.categorieCode || '';
        const reference = item.reclamation?.reference || '';
        const sousCat = item.reclamation?.sousCategorieLibelle || '';
        const description = item.reclamation?.description || '';

        // Test de recherche textuelle sur l'ensemble des champs pertinents
        const matchesSearch = 
          nom.toLowerCase().includes(search) ||
          prenom.toLowerCase().includes(search) ||
          numClient.toLowerCase().includes(search) ||
          categorieCode.toLowerCase().includes(search) ||
          reference.toLowerCase().includes(search) ||
          sousCat.toLowerCase().includes(search) ||
          description.toLowerCase().includes(search);

        // Test du filtre par statut
        const matchesStatus = 
          this.selectedStatusFilter === 'TOUS' || 
          !this.selectedStatusFilter ||
          item.reclamation?.statut === this.selectedStatusFilter;

        return matchesSearch && matchesStatus;
      });
    }

    // zone.component.ts
  chargerRendezVous(): void {
    this.reclamationService.getRendezVous(0, 20).subscribe({
      next: (pageData) => {
        console.log('GET /api/rendez-vous response:', pageData);
        this.rendezVousList = Array.isArray(pageData?.content) ? pageData.content : [];
        console.log('Rendez-vous chargés :', this.rendezVousList);
      },
      error: (err) => {
        console.error('Error on GET /api/rendez-vous:', err);
        this.rendezVousList = [];
      }
    });
  }

  marquerRendezVousRealise(): void {
    if (!this.selectedRendezVousId) {
      alert('Veuillez sélectionner un rendez-vous valide.');
      return;
    }

    this.reclamationService.confirmerRealisationRendezVous(this.selectedRendezVousId).subscribe({
      next: () => {
        this.rendezVousRealise = true;
        const rdv = this.rendezVousList.find((r) => r.id === this.selectedRendezVousId);
        const clientId = this.selectedReclamationDetails?.client?.clientId;
        const rdvDuClient = clientId
          ? this.rendezVousList.filter((rdv) => rdv.clientId === clientId)
          : this.rendezVousList;

        if (rdv) {
          rdv.statut = 'REALISE';
        }
        alert(`Rendez-vous #${this.selectedRendezVousId} marqué comme réalisé !`);
      },
      error: (err) => {
        console.error('Erreur confirmation réalisation RDV :', err);
        alert('Impossible de confirmer la réalisation du rendez-vous.');
      }
    });
  }    


confirmerCloture(reclamationId: number | undefined): void {
  const motif = this.motifCloture?.trim();
  if (!reclamationId || !motif) return;

  this.reclamationService.cloturerReclamation(reclamationId, motif).subscribe({
    next: (updated) => {
      this.motifCloture = '';
      if (this.selectedReclamationDetails?.reclamation) {
        this.selectedReclamationDetails.reclamation.statut = updated?.statut ?? 'CLOTUREE';
      }
      // Optionnel : mettre à jour la liste principale des réclamations
    },
    error: (err) => {
      console.error('Erreur lors de la clôture de la réclamation :', err);
    }
  });
}

}