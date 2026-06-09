import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar';
import { AgentPayoutService, CashBalanceDTO, OperationCaisseDTO, CashCloseResponseDTO } from '../../../../core/services/agent-payout.service';

@Component({
  selector: 'app-caisse',
  imports: [FormsModule, CommonModule, DatePipe, NavbarComponent],
  templateUrl: './caisse.html',
  styleUrl: './caisse.css',
})
export class Caisse implements OnInit {
  balance: CashBalanceDTO | null = null;
  operations: OperationCaisseDTO[] = [];
  loading = false;
  error = '';

  showClotureForm = false;
  soldeReel: number | null = null;
  clotureResult: CashCloseResponseDTO | null = null;
  clotureLoading = false;

  selectedDate = new Date().toISOString().split('T')[0];

  constructor(private svc: AgentPayoutService) {}

  ngOnInit() { this.loadBalance(); this.loadOperations(); }

  loadBalance() {
    this.svc.getBalance().subscribe({ next: (b) => this.balance = b });
  }

  loadOperations() {
    this.loading = true;
    this.svc.getOperations(this.selectedDate).subscribe({
      next: (ops) => { this.operations = ops; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  cloture() {
    if (this.soldeReel === null) return;
    this.clotureLoading = true;
    this.error = '';
    this.svc.closeCash(this.soldeReel).subscribe({
      next: (r) => {
        this.clotureResult = r;
        this.clotureLoading = false;
        this.showClotureForm = false;
        this.loadBalance();
      },
      error: (e) => {
        this.error = e.error?.message || 'Erreur lors de la clôture.';
        this.clotureLoading = false;
      }
    });
  }

  typeLabel(type: string): string {
    const map: Record<string, string> = {
      ENVOI: 'Envoi',
      RETRAIT: 'Retrait',
      DEPOT: 'Dépôt',
      RETRAIT_INTERNE: 'Retrait interne',
    };
    return map[type] ?? type;
  }

  typeClass(type: string): string {
    return ['RETRAIT', 'RETRAIT_INTERNE'].includes(type) ? 'debit' : 'credit';
  }

  get ecartClass(): string {
    if (!this.clotureResult) return '';
    return this.clotureResult.ecart < 0 ? 'negative' : this.clotureResult.ecart > 0 ? 'positive' : 'zero';
  }
}
