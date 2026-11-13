import { Component } from '@angular/core';
import {Router} from "@angular/router";

interface Step {
  title: string;
  isOpen: boolean;
  content: string[];
}

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})

export class SettingsComponent {

  // Gestion de la modal de l'image
  isImageZoomed = false;
  imageSrc = 'assets/images/hexabyte-manual.jpg'; // Remplacez par le chemin de votre image

  // Contenu des étapes déroulantes
  steps: Step[] = [
    {
      title: '1. Espace Client : Connexion & Session',
      isOpen: true,
      content: [
        'Inscription et authentification sécurisée sur l\'application.',
        'Compte à rebours de session active fixé à 1 heure.',
        'Accès aux ressources de l\'application et au compte personnel.'
      ]
    },
    {
      title: '2. Déposer une Réclamation',
      isOpen: false,
      content: [
        'Option 1 : Réclamation directe contre le service courant ou l\'application.',
        'Option 2 : Réclamation réseau (Fournisseur d\'accès Hexabyte).',
        'Condition : Le client doit être connecté pour recevoir un Token de validation.',
        'Statut initial de la réclamation : OUVERTE.'
      ]
    },
    {
      title: '3. Assistance IA & Évaluation',
      isOpen: false,
      content: [
        'Échange initial avec le Chatbot IA Hexabyte pour diagnostic rapide.',
        'L\'ensemble de la conversation est sauvegardé dans le compte.',
        'Évaluation de la réponse : Si Note > 2/5, le problème est RESOLU par l\'IA.',
        'Si Note <= 2/5, possibilité d\'escalade manuelle ou automatique vers le support.'
      ]
    },
    {
      title: '4. Support Humain & Priorités',
      isOpen: false,
      content: [
        'Prise en charge par un Agent Support.',
        'Attribution du niveau de priorité à tout moment : BASSE, MOYENNE, HAUTE.'
      ]
    },
    {
      title: '5. Intervention Technicien (Hors Système)',
      isOpen: false,
      content: [
        'Assignation de la réclamation à un agent technicien.',
        'Déroulement dans la vraie vie : Prise de rendez-vous et rencontre physique sur le terrain.'
      ]
    },
    {
      title: '6. Administration & Supervision',
      isOpen: false,
      content: [
        'Vue globale sur toutes les réclamations du système.',
        'Possibilité de modifier le niveau de compétence d\'un technicien ou d\'appliquer des sanctions.'
      ]
    }
  ];

  toggleImageZoom() {
    this.isImageZoomed = !this.isImageZoomed;
  }

  toggleStep(index: number) {
    this.steps[index].isOpen = !this.steps[index].isOpen;
  }

}
