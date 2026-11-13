import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ReclamationService } from '../../services/reclamation.service';
import { Reclamation } from '../../services/reclamation.model';

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
export class ZoneComponent implements OnInit, OnDestroy {
  
  // Injection propre des services
  private reclamationService = inject(ReclamationService);


  agentName: string = 'Karim Mansour';
  agentRole: string = 'Agent Support Senior';

  currentTimeFormatted: string = '';
  private clockInterval: any;

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
  // Adaptez le type Client selon votre modèle s'il est inclus dans SupportReclamationResponse
  selectedClient: any | null = null; 
  selectedReclamationRef: string = '';

  items: Reclamation[] = [];

  ngOnInit(): void {
    this.updateClock();
    this.clockInterval = setInterval(() => this.updateClock(), 1000);
    this.chargerReclamations();
  }

  ngOnDestroy(): void {
    if (this.clockInterval) clearInterval(this.clockInterval);
  }

chargerReclamations(): void {
    this.loading = true;
    
    this.reclamationService.toutesPourSupport('ESCALADEE_SUPPORT').subscribe({
      next: (data) => {
        // data est directement le tableau [ {reclamation, client}, ... ]
        this.items = data || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement réclamations support', err);
        this.errorMessage = 'Impossible de contacter le serveur Hexabyte.';
        this.loading = false;
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

  get unreadNotificationsCount(): number {
    return this.notifications.filter(n => n.unread).length;
  }

  markAllAsRead(): void {
    this.notifications.forEach(n => n.unread = false);
  }

  get filteredItems(): Reclamation[] {
    return this.items.filter(item => {
      
      // Ajustez selon la structure exacte de votre DTO SupportReclamationResponse (ex: item.nomClient, item.reference, etc.)
      const search = this.searchTerm.toLowerCase();

      const nom = item.client?.nom || '';
      const prenom = item.client?.prenom || '';
      const numClient = item.client?.numeroClient || '';
      const reference = item.reference || '';
      const sousCat = item.sousCategorieLibelle || '';

      const matchesSearch = 
        nom.toLowerCase().includes(search) ||
        prenom.toLowerCase().includes(search) ||
        numClient.toLowerCase().includes(search) ||
        reference.toLowerCase().includes(search) ||
        sousCat.toLowerCase().includes(search);

      const matchesStatus = 
        this.selectedStatusFilter === 'TOUS' || 
        item.statut === this.selectedStatusFilter;

      return matchesSearch && matchesStatus;
    });
  }
}