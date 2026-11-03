import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
 
import { AuthService } from '../../../auth/auth.service';
import { ClientProfile, UpdateProfileRequest } from '../../client.model';
import { ClientService } from '../../client.service';
import { Reclamation } from '../../../reclamation/reclamation.model';
import { ReclamationService } from '../../../reclamation/reclamation.service';
import { SessionService } from '../../../core/services/session.service';
import { toApiError } from '../../../core/utils/api-error.util';
 
type Statut = Reclamation['statut'];
 
@Component({
  selector: 'app-client-espace',
  templateUrl: './client-espace.component.html',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  styleUrls: ['./client-espace.component.css']
})



export class ClientEspaceComponent {

  // ----------------------------- Profil -----------------------------
  profile: ClientProfile | null = null;
  loadingProfile = true;
  profileError = '';
  profileNotice = '';
  editing = false;
  saving = false;
  form = { telephone: '', adresseInstallation: '', gouvernorat: '' };
  formErrors: Record<string, string> = {};
 
  // ----------------------------- Réclamations -----------------------------
  reclamations: Reclamation[] = [];
  totalReclamations = 0;
  loadingReclamations = true;
  reclamationsError = '';
  search = '';
  statutFiltre: '' | Statut = '';
  page = 0;
  readonly pageSize = 5;
 
  readonly statuts: { value: '' | Statut; label: string }[] = [
    { value: '', label: 'All statuses' },
    { value: 'OUVERTE', label: 'Open' },
    { value: 'EN_COURS_IA', label: 'In progress' },
    { value: 'RESOLUE_PAR_IA', label: 'Resolved' },
    { value: 'ESCALADEE_SUPPORT', label: 'Transferred to an advisor' },
    { value: 'CLOTUREE', label: 'Closed' },
  ];
 
  readonly gouvernorats: string[] = [
    'Ariana', 'Béja', 'Ben Arous', 'Bizerte', 'Gabès', 'Gafsa', 'Jendouba', 'Kairouan',
    'Kasserine', 'Kébili', 'Le Kef', 'Mahdia', 'La Manouba', 'Médenine', 'Monastir', 'Nabeul',
    'Sfax', 'Sidi Bouzid', 'Siliana', 'Sousse', 'Tataouine', 'Tozeur', 'Tunis', 'Zaghouan',
  ];
 
  constructor(
    private clientService: ClientService,
    private reclamationService: ReclamationService,
    private authService: AuthService,
    public session: SessionService,
    private router: Router,
  ) {}
 
  ngOnInit(): void {
    if (!this.session.isLoggedIn()) {
      this.loadingProfile = false;      // le gabarit affiche l'invitation à se connecter
      this.loadingReclamations = false;
      return;
    }
    this.chargerProfil();
    this.chargerReclamations();
  }
 
  // ----------------------------- Profil : lecture et modification -----------------------------
 
  private chargerProfil(): void {
    this.clientService.getProfile().subscribe({
      next: (profile) => { this.profile = profile; this.loadingProfile = false; },
      error: (err: HttpErrorResponse) => {
        this.loadingProfile = false;
        this.profileError = toApiError(err).message;
      },
    });
  }
 
  startEdit(): void {
    if (!this.profile) { return; }
    this.form = {
      telephone: this.profile.telephone ?? '',
      adresseInstallation: this.profile.adresseInstallation ?? '',
      gouvernorat: this.profile.gouvernorat ?? '',
    };
    this.formErrors = {};
    this.profileError = '';
    this.profileNotice = '';
    this.editing = true;
  }
 
  cancelEdit(): void {
    this.editing = false;
    this.formErrors = {};
  }
 
  /** PATCH /api/clients/me : on n'envoie que les champs réellement modifiés. */
  save(): void {
    if (!this.profile || this.saving) { return; }
    this.formErrors = this.valider(this.profile);
    if (Object.keys(this.formErrors).length > 0) { return; }
 
    const changes = this.champsModifies(this.profile);
    if (Object.keys(changes).length === 0) {
      this.editing = false;
      this.profileNotice = 'No changes to save.';
      return;
    }
 
    this.saving = true;
    this.profileError = '';
    this.clientService.updateProfile(changes).subscribe({
      next: (profile) => {
        this.profile = profile;
        this.saving = false;
        this.editing = false;
        this.profileNotice = 'Your information has been updated.';
      },
      error: (err: HttpErrorResponse) => {
        this.saving = false;
        this.profileError = toApiError(err).message;
      },
    });
  }
 
  /**
   * Le serveur ne contrôle pas encore le format du téléphone dans le PATCH :
   * on valide ici avant d'envoyer, pour ne jamais lui transmettre une valeur invalide.
   */
  private valider(p: ClientProfile): Record<string, string> {
    const erreurs: Record<string, string> = {};
    const tel = this.form.telephone.replace(/\s/g, '');
    const adresse = this.form.adresseInstallation.trim();
 
    if (tel === '' && p.telephone) {
      erreurs['telephone'] = 'Phone number cannot be empty.';
    } else if (tel !== '' && !/^(\+216)?[0-9]{8}$/.test(tel)) {
      erreurs['telephone'] = 'Phone number must be 8 digits (with optional +216).';
    }
    if (adresse === '' && p.adresseInstallation) {
      erreurs['adresseInstallation'] = 'Installation address cannot be empty.';
    } else if (adresse.length > 255) {
      erreurs['adresseInstallation'] = 'Address is too long (255 characters max).';
    }
    return erreurs;
  }
 
  private champsModifies(p: ClientProfile): UpdateProfileRequest {
    const tel = this.form.telephone.replace(/\s/g, '');
    const adresse = this.form.adresseInstallation.trim();
    const gouvernorat = this.form.gouvernorat;
    const changes: UpdateProfileRequest = {};
 
    if (tel !== '' && tel !== (p.telephone ?? '')) { changes.telephone = tel; }
    if (adresse !== '' && adresse !== (p.adresseInstallation ?? '')) { changes.adresseInstallation = adresse; }
    if (gouvernorat !== '' && gouvernorat !== (p.gouvernorat ?? '')) { changes.gouvernorat = gouvernorat; }
    return changes;
  }
 
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
 
  // ----------------------------- Réclamations : recherche, filtre, pagination -----------------------------
 
  /** Le client charge ses 100 plus récentes réclamations ; la recherche et le filtre se font ici. */
  private chargerReclamations(): void {
    this.reclamationService.lister(0, 100).subscribe({
      next: (page) => {
        this.reclamations = page.content;
        this.totalReclamations = page.totalElements;
        this.loadingReclamations = false;
      },
      error: (err: HttpErrorResponse) => {
        this.loadingReclamations = false;
        this.reclamationsError = toApiError(err).message;
      },
    });
  }
 
  get filtrees(): Reclamation[] {
    const q = this.search.trim().toLowerCase();
    return this.reclamations.filter(r =>
      (this.statutFiltre === '' || r.statut === this.statutFiltre) &&
      (q === '' ||
        r.reference.toLowerCase().includes(q) ||
        r.categorieLibelle.toLowerCase().includes(q) ||
        r.sousCategorieLibelle.toLowerCase().includes(q) ||
        r.description.toLowerCase().includes(q)));
  }
 
  get pageCourante(): Reclamation[] {
    return this.filtrees.slice(this.page * this.pageSize, (this.page + 1) * this.pageSize);
  }
 
  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filtrees.length / this.pageSize));
  }
 
  onFiltreChange(): void { this.page = 0; }
  pagePrecedente(): void { if (this.page > 0) { this.page--; } }
  pageSuivante(): void { if (this.page < this.totalPages - 1) { this.page++; } }
 
  statutLibelle(statut: Statut): string {
    return this.statuts.find(s => s.value === statut)?.label ?? statut;
  }
 
  statutClasse(statut: Statut): string {
    switch (statut) {
      case 'EN_COURS_IA': return 'bg-blue-100 text-blue-800';
      case 'RESOLUE_PAR_IA': return 'bg-green-100 text-green-800';
      case 'ESCALADEE_SUPPORT': return 'bg-orange-100 text-orange-800';
      default: return 'bg-gray-200 text-gray-800';
    }
  }
}
