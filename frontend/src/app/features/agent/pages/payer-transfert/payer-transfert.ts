import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar';
import { TransfertService } from '../../../../core/services/transfert';

@Component({
  selector: 'app-payer-transfert',
  imports: [FormsModule, CommonModule, NavbarComponent],
  templateUrl: './payer-transfert.html',
  styleUrl: './payer-transfert.css',
})
export class PayerTransfert {
  searchCode = '';
  searchPhone = '';
  transfer: any = null;
  identityNumber = '';
  identityType = 'CIN';
  error = '';
  success = '';
  loading = false;

  // TODO: récupérer depuis le token JWT
  agentId = 1;

  constructor(private transfertService: TransfertService) {}

  search() {
    this.error = '';
    this.transfer = null;
    this.loading = true;
    this.transfertService.search(this.searchCode, this.searchPhone).subscribe({
      next: (res) => {
        this.transfer = Array.isArray(res) ? res[0] : res;
        if (!this.transfer) this.error = 'Transfert introuvable';
        this.loading = false;
      },
      error: () => {
        this.error = 'Transfert introuvable';
        this.loading = false;
      }
    });
  }

  confirmPayout() {
    if (!this.identityNumber) { this.error = 'Pièce d\'identité obligatoire'; return; }
    this.loading = true;
    this.transfertService.payout(this.transfer.id, this.agentId, this.identityNumber, this.identityType).subscribe({
      next: () => {
        this.success = 'Paiement effectué avec succès !';
        this.transfer = null;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Erreur lors du paiement';
        this.loading = false;
      }
    });
  }

  reset() {
    this.searchCode = '';
    this.searchPhone = '';
    this.transfer = null;
    this.identityNumber = '';
    this.error = '';
    this.success = '';
  }
}
