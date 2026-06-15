import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CurrencyService, CurrencyResponse, CurrencyRequest } from '../../../../core/services/currency.service';
import { CorridorService, CorridorResponse } from '../../../../core/services/corridor.service';

@Component({
  selector: 'app-currency-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './currency-management.html',
  styleUrls: ['./currency-management.css']
})
export class CurrencyManagementComponent implements OnInit {

  // ── Devises ───────────────────────────────────────────────────────────────
  currencies: CurrencyResponse[] = [];

  showModal = false;
  isEditMode = false;
  selectedCurrencyId: number | null = null;
  formError = '';

  formData: CurrencyRequest = {
    code: '', name: '', symbol: '', active: true
  };

  showDeleteConfirm = false;
  currencyToDeleteId: number | null = null;
  currencyToDeleteName = '';

  // ── Corridors ─────────────────────────────────────────────────────────────
  corridors: CorridorResponse[] = [];
  corridorsLoading = false;
  corridorToggleLoadingId: number | null = null;

  // ── Notifications ─────────────────────────────────────────────────────────
  successMessage = '';
  errorMessage = '';

  constructor(
    private currencyService: CurrencyService,
    private corridorService: CorridorService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCurrencies();
    this.loadCorridors();
  }

  // ════════════════════════════════════════════════════════
  //  DEVISES
  // ════════════════════════════════════════════════════════

  loadCurrencies(): void {
    this.currencyService.getAllCurrencies().subscribe({
      next: (data) => {
        this.currencies = data;
        this.cdr.detectChanges();
        setTimeout(() => this.drawCharts(), 100);
      },
      error: () => this.showError('Erreur lors du chargement des devises.')
    });
  }

  private drawCharts(): void {
    // Placeholder pour la logique de dessin éventuelle.
    // Si la page n'utilise pas de graphiques, cette méthode peut rester vide.
  }

  openAddModal(): void {
    this.isEditMode = false;
    this.selectedCurrencyId = null;
    this.formData = { code: '', name: '', symbol: '', active: true };
    this.formError = '';
    this.showModal = true;
  }

  openEditModal(currency: CurrencyResponse): void {
    this.isEditMode = true;
    this.selectedCurrencyId = currency.id;
    this.formData = {
      code: currency.code,
      name: currency.name,
      symbol: currency.symbol,
      active: currency.active
    };
    this.formError = '';
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.formError = '';
  }

  saveForm(): void {
    if (!this.formData.code || !this.formData.name || !this.formData.symbol) {
      this.formError = 'Veuillez remplir tous les champs obligatoires.';
      return;
    }
    if (this.isEditMode && this.selectedCurrencyId !== null) {
      this.currencyService.updateCurrency(this.selectedCurrencyId, this.formData).subscribe({
        next: () => { this.closeModal(); this.loadCurrencies(); this.showSuccess('Devise mise à jour.'); },
        error: () => { this.formError = 'Erreur lors de la mise à jour.'; }
      });
    } else {
      this.currencyService.createCurrency(this.formData).subscribe({
        next: () => { this.closeModal(); this.loadCurrencies(); this.showSuccess('Devise créée avec succès.'); },
        error: () => { this.formError = 'Erreur lors de la création.'; }
      });
    }
  }

  confirmDesactivate(currency: CurrencyResponse): void {
    this.currencyToDeleteId = currency.id;
    this.currencyToDeleteName = currency.name;
    this.showDeleteConfirm = true;
  }

  cancelDesactivate(): void {
    this.showDeleteConfirm = false;
    this.currencyToDeleteId = null;
    this.currencyToDeleteName = '';
  }

  executeDesactivate(): void {
    if (this.currencyToDeleteId === null) return;
    this.currencyService.deactivateCurrency(this.currencyToDeleteId).subscribe({
      next: () => { this.cancelDesactivate(); this.loadCurrencies(); this.showSuccess('Devise supprimée.'); },
      error: () => { this.cancelDesactivate(); this.showError('Erreur lors de la suppression.'); }
    });
  }

  // ════════════════════════════════════════════════════════
  //  CORRIDORS
  // ════════════════════════════════════════════════════════

  loadCorridors(): void {
    this.corridorsLoading = true;
    this.corridorService.getAllCorridors().subscribe({
      next: (data) => {
        this.corridors = data;
        this.corridorsLoading = false;
      },
      error: () => {
        this.showError('Erreur lors du chargement des corridors.');
        this.corridorsLoading = false;
      }
    });
  }

  toggleCorridor(corridor: CorridorResponse): void {
    if (this.corridorToggleLoadingId === corridor.id) return;
    this.corridorToggleLoadingId = corridor.id;

    this.corridorService.toggleCorridor(corridor.id).subscribe({
      next: (updated) => {
        const index = this.corridors.findIndex(c => c.id === updated.id);
        if (index !== -1) {
          this.corridors[index] = updated;
        }
        this.corridorToggleLoadingId = null;
        const etat = updated.active ? 'activé' : 'désactivé';
        this.showSuccess(`Corridor ${updated.sourceCurrencyCode} → ${updated.destinationCurrencyCode} ${etat}.`);
      },
      error: () => {
        this.corridorToggleLoadingId = null;
        this.showError('Erreur lors du changement de statut du corridor.');
      }
    });
  }

  isToggleLoading(corridorId: number): boolean {
    return this.corridorToggleLoadingId === corridorId;
  }

  // ════════════════════════════════════════════════════════
  //  NOTIFICATIONS
  // ════════════════════════════════════════════════════════

  private showSuccess(msg: string): void {
    this.successMessage = msg;
    setTimeout(() => this.successMessage = '', 3000);
  }

  private showError(msg: string): void {
    this.errorMessage = msg;
    setTimeout(() => this.errorMessage = '', 4000);
  }
}