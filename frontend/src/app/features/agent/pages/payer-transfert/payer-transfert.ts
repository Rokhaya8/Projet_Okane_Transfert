import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar';
import { AgentPayoutService, TransferSearchDTO, PayoutReceiptDTO } from '../../../../core/services/agent-payout.service';

@Component({
  selector: 'app-payer-transfert',
  imports: [FormsModule, CommonModule, DatePipe, NavbarComponent],
  templateUrl: './payer-transfert.html',
  styleUrl: './payer-transfert.css',
})
export class PayerTransfert {
  searchCode = '';
  searchTel = '';
  loading = false;
  error = '';

  found: TransferSearchDTO | null = null;
  pieceIdentite = '';

  receipt: PayoutReceiptDTO | null = null;

  constructor(private svc: AgentPayoutService) {}

  search() {
    if (!this.searchCode.trim() && !this.searchTel.trim()) return;
    this.loading = true;
    this.error = '';
    this.found = null;
    this.receipt = null;
    this.svc.searchTransfer(this.searchCode || undefined, this.searchTel || undefined).subscribe({
      next: (list) => {
        this.loading = false;
        this.found = list.length ? list[0] : null;
        if (!this.found) this.error = 'Aucun transfert trouvé.';
      },
      error: () => {
        this.loading = false;
        this.error = 'Transfert introuvable.';
      },
    });
  }

  pay() {
    if (!this.found || !this.pieceIdentite.trim()) return;
    this.loading = true;
    this.error = '';
    this.svc.payoutTransfer(this.found.id, this.pieceIdentite).subscribe({
      next: (r) => {
        this.loading = false;
        this.receipt = r;
        this.found = null;
      },
      error: (e) => {
        this.loading = false;
        this.error = e.error?.message || 'Erreur lors du paiement.';
      },
    });
  }

  reset() {
    this.searchCode = '';
    this.searchTel = '';
    this.found = null;
    this.receipt = null;
    this.pieceIdentite = '';
    this.error = '';
  }

  statutClass(s: string) {
    return s === 'EN_ATTENTE' ? 'pending' : s === 'PAYÉ' ? 'paid' : 'cancelled';
  }
}
