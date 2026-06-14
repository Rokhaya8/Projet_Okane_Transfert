import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { Transfert } from '../../../../core/services/transfert';
import {
  CashAdjustmentRequest,
  CashSessionResponse,
  CloseCashSessionRequest,
  OpenCashSessionRequest,
} from '../../../../shared/models/transfert';
import { NavbarComponent } from '../../components/navbar/navbar';

@Component({
  standalone: true,
  selector: 'app-caisse',
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './caisse.html',
  styleUrl: './caisse.css',
})
export class Caisse implements OnInit {
  readonly context;
  session: CashSessionResponse | null = null;
  openingBalance = 10000;
  adjustAmount = 0;
  adjustReference = '';
  countedAmount = 0;
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private transfertService: Transfert) {
    this.context = this.transfertService.getAgentContext();
  }

  ngOnInit(): void {
    this.loadCurrentSession();
  }

  get canOpenSession(): boolean {
    return !this.loading && this.openingBalance > 0;
  }

  get canAdjustSession(): boolean {
    return !!this.session && !this.loading && this.adjustAmount > 0 && this.adjustReference.trim().length > 0;
  }

  loadCurrentSession(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.transfertService
      .getCurrentCashSession(this.context.agentId)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (response) => {
          this.session = response;
          this.countedAmount = response.currentBalance;
        },
        error: () => {
          this.session = null;
        },
      });
  }

  openSession(): void {
    if (this.openingBalance <= 0) {
      this.errorMessage = 'Le solde d ouverture doit etre superieur a 0.';
      this.successMessage = '';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: OpenCashSessionRequest = {
      agentId: this.context.agentId,
      agencyId: this.context.agencyId,
      openingBalance: this.openingBalance,
    };

    this.transfertService
      .openCashSession(request)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (response) => {
          this.session = response;
          this.countedAmount = response.currentBalance;
          this.successMessage = 'La caisse a ete ouverte.';
        },
        error: (err) => {
          this.errorMessage = this.getErrorMessage(err, 'Impossible d ouvrir la caisse.');
        },
      });
  }

  adjustSession(): void {
    if (!this.session) {
      this.errorMessage = 'Aucune caisse ouverte.';
      this.successMessage = '';
      return;
    }

    if (this.adjustAmount <= 0) {
      this.errorMessage = 'Le montant a ajouter doit etre superieur a 0.';
      this.successMessage = '';
      return;
    }

    if (!this.adjustReference.trim()) {
      this.errorMessage = 'La reference est requise.';
      this.successMessage = '';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: CashAdjustmentRequest = {
      amount: this.adjustAmount,
      reference: this.adjustReference.trim(),
    };

    this.transfertService
      .adjustCurrentCashSession(this.context.agentId, request)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (response) => {
          this.session = response;
          this.countedAmount = response.currentBalance;
          this.successMessage = 'Montant ajoute a la caisse.';
          this.adjustAmount = 0;
          this.adjustReference = '';
        },
        error: (err) => {
          this.errorMessage = this.getErrorMessage(err, 'Impossible d ajouter de l argent a la caisse.');
        },
      });
  }

  closeSession(): void {
    if (!this.session) {
      this.errorMessage = 'Aucune caisse ouverte.';
      this.successMessage = '';
      return;
    }

    if (this.countedAmount < 0) {
      this.errorMessage = 'Le montant compte ne peut pas etre negatif.';
      this.successMessage = '';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: CloseCashSessionRequest = {
      countedAmount: this.countedAmount,
    };

    this.transfertService
      .closeCurrentCashSession(this.context.agentId, request)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (response) => {
          this.session = null;
          this.openingBalance = response.countedAmount ?? response.currentBalance;
          this.countedAmount = 0;
          this.successMessage = 'La caisse a ete cloturee.';
        },
        error: (err) => {
          this.errorMessage = this.getErrorMessage(err, 'Impossible de cloturer la caisse.');
        },
      });
  }

  private getErrorMessage(err: any, fallback: string): string {
    if (err?.name === 'TimeoutError') {
      return 'Le backend ne repond pas. Verifie que le serveur Java est demarre sur le port 8080.';
    }

    return err?.error?.message || fallback;
  }
}
