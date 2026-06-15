import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { NavbarComponent } from '../../components/navbar/navbar';
import { Transfert } from '../../../../core/services/transfert';
import { PayTransferRequest, SearchTransferResponse } from '../../../../shared/models/transfert';

@Component({
  standalone: true,
  selector: 'app-payer-transfert',
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './payer-transfert.html',
  styleUrl: './payer-transfert.css',
})
export class PayerTransfert {
  readonly context;
  searchType: 'code' | 'phone' = 'code';
  searchTerm = '';
  searchResults: SearchTransferResponse[] = [];
  selectedTransfer: SearchTransferResponse | null = null;
  beneficiaryIdentityNumber = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private transfertService: Transfert) {
    this.context = this.transfertService.getAgentContext();
  }

  search(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.selectedTransfer = null;
    this.searchResults = [];

    const term = this.searchTerm.trim();
    if (!term) {
      this.errorMessage = 'Veuillez saisir un code ou un numéro de téléphone.';
      return;
    }

    this.loading = true;
    if (this.searchType === 'code') {
      this.transfertService.searchByCode(term)
        .pipe(finalize(() => (this.loading = false)))
        .subscribe({
          next: (transfer) => { this.selectedTransfer = transfer; },
          error: () => { this.errorMessage = 'Aucun transfert trouvé avec ce code.'; },
        });
    } else {
      this.transfertService.searchByPhone(term)
        .pipe(finalize(() => (this.loading = false)))
        .subscribe({
          next: (transfers) => {
            this.searchResults = transfers;
            if (transfers.length === 1) this.selectedTransfer = transfers[0];
          },
          error: () => { this.errorMessage = 'Aucun transfert trouvé pour ce numéro.'; },
        });
    }
  }

  selectTransfer(transfer: SearchTransferResponse): void {
    this.selectedTransfer = transfer;
    this.searchResults = [];
    this.successMessage = '';
    this.errorMessage = '';
  }

  pay(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.selectedTransfer) {
      this.errorMessage = 'Veuillez sélectionner un transfert à payer.';
      return;
    }
    if (!this.beneficiaryIdentityNumber.trim()) {
      this.errorMessage = "Veuillez saisir le numéro d'identité du bénéficiaire.";
      return;
    }
    if (!this.selectedTransfer.payable) {
      this.errorMessage = 'Ce transfert ne peut pas être payé (statut: ' + this.selectedTransfer.status + ').';
      return;
    }

    this.loading = true;
    const paidCode = this.selectedTransfer.referenceCode;
    const request: PayTransferRequest = {
      agentId: this.context.agentId,
      referenceCode: paidCode,
      beneficiaryIdentityNumber: this.beneficiaryIdentityNumber.trim(),
    };

    this.transfertService.payTransfer(request)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: () => {
          this.successMessage = `Le retrait ${paidCode} a été payé avec succès.`;
          this.beneficiaryIdentityNumber = '';
          this.searchTerm = '';
          this.selectedTransfer = null;
        },
        error: (err) => {
          this.errorMessage = err?.error?.message || err?.message || 'Impossible de payer le retrait.';
        },
      });
  }
}
