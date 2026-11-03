import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { Categorie, SousCategorie } from '../../categorie/categorie.model';
import { CategorieService } from '../../categorie/categorie.service';
import { SessionService } from '../../core/services/session.service';
import { toApiError } from '../../core/utils/api-error.util';
import { Reclamation } from '../reclamation.model';
import { ReclamationService } from '../reclamation.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-reclamation-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reclamation-form.component.html'
})
export class ReclamationFormComponent implements OnInit {

  /** Émis après un dépôt réussi : le composant parent peut rafraîchir sa liste. */
  @Output() created = new EventEmitter<Reclamation>();

  categories: Categorie[] = [];
  categorieId: number | null = null;
  sousCategorieId: number | null = null;
  description = '';

  loadingCategories = true;
  loading = false;
  reclamationCreee: Reclamation | null = null;
  errorMessage = '';
  fieldErrors: Record<string, string> = {};

  constructor(
    private categorieService: CategorieService,
    private reclamationService: ReclamationService,
    public session: SessionService,
  ) {}

  ngOnInit(): void {
    this.categorieService.lister().subscribe({
      next: (categories) => { this.categories = categories; this.loadingCategories = false; },
      error: (err: HttpErrorResponse) => {
        this.loadingCategories = false;
        this.errorMessage = toApiError(err).message;
      },
    });
  }

  /** Sous-catégories de la catégorie choisie. */
  get sousCategories(): SousCategorie[] {
    return this.categories.find(c => c.id === this.categorieId)?.sousCategories ?? [];
  }

  onCategorieChange(): void {
    this.sousCategorieId = null;      // l'ancienne sous-catégorie n'appartient plus à la catégorie
  }

  onSubmit(form: NgForm): void {
    this.errorMessage = '';
    this.fieldErrors = {};
    this.reclamationCreee = null;

    if (form.invalid || this.sousCategorieId === null || this.loading) { return; }
    if (!this.session.isLoggedIn()) {
      this.errorMessage = 'Your session has expired. Please log in again.';
      return;
    }

    this.loading = true;
    this.reclamationService.creer({
      sousCategorieId: this.sousCategorieId,
      description: this.description.trim(),
    }).subscribe({
      next: (reclamation) => {
        this.loading = false;
        this.reclamationCreee = reclamation;
        this.created.emit(reclamation);
        this.reinitialiser(form);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const apiError = toApiError(err);
        this.errorMessage = apiError.message;
        this.fieldErrors = apiError.fieldErrors;
      },
    });
  }

  private reinitialiser(form: NgForm): void {
    form.resetForm({ categorieId: null, sousCategorieId: null, description: '' });
    this.categorieId = null;
    this.sousCategorieId = null;
    this.description = '';
  }
}