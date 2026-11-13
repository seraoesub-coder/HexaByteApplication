import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface Technician {
  nom: string;
  prenom: string;
  telephone: string;
  expertise: number;
  disponible: boolean;
  avatar: string;
}

@Component({
  selector: 'app-credit',
  templateUrl: './credit.component.html',
  styleUrls: ['./credit.component.css']
})

export class CreditComponent {

  // Tableau récapitulatif de vos techniciens avec leurs compétences et avatars professionnels
  technicians: Technician[] = [

    { nom: 'Ben Salah', prenom: 'Mohamed', telephone: '20111001', expertise: 5, disponible: true, avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80' },
    { nom: 'Jebali', prenom: 'Karim', telephone: '21111002', expertise: 4, disponible: true, avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80' },
    { nom: 'Gharbi', prenom: 'Sonia', telephone: '22111003', expertise: 3, disponible: true, avatar: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop&q=80' },
    { nom: 'Mansouri', prenom: 'Walid', telephone: '50111004', expertise: 2, disponible: true, avatar: 'https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=150&auto=format&fit=crop&q=80' },
    { nom: 'Chebbi', prenom: 'Rania', telephone: '52111005', expertise: 4, disponible: true, avatar: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=150&auto=format&fit=crop&q=80' },
    { nom: 'Haddad', prenom: 'Anis', telephone: '55111006', expertise: 1, disponible: true, avatar: 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=150&auto=format&fit=crop&q=80' },
    { nom: 'Bouaziz', prenom: 'Ines', telephone: '95111007', expertise: 3, disponible: true, avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80' },
    { nom: 'Khelifi', prenom: 'Slim', telephone: '27547896', expertise: 5, disponible: true, avatar: 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150&auto=format&fit=crop&q=80' },
    { nom: 'Mhidi', prenom: 'Arbi', telephone: '55420609', expertise: 2, disponible: true, avatar: 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150&auto=format&fit=crop&q=80' },
    { nom: 'Hosni', prenom: 'Farid', telephone: '98745600', expertise: 1, disponible: false, avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&auto=format&fit=crop&q=80' },
    { nom: 'Jomaaa', prenom: 'Nomeene', telephone: '32549004', expertise: 1, disponible: true, avatar: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&auto=format&fit=crop&q=80' }
 
  ];

  assignTech(tech: Technician) {
    alert(`Demande d'intervention envoyée à ${tech.prenom} ${tech.nom} !`);
  }
}
