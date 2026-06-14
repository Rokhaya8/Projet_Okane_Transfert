import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Transfert } from '../../../../core/services/transfert';
import { CashOperation, TransferPayment } from '../../../../shared/models/transfert';
import { NavbarComponent } from '../../components/navbar/navbar';

@Component({
  standalone: true,
  selector: 'app-agent-historique',
  imports: [CommonModule, DatePipe, FormsModule, NavbarComponent],
  templateUrl: './historique.html',
  styleUrl: './historique.css',
})
export class AgentHistorique implements OnInit {
  activeTab: 'payments' | 'cash' = 'payments';
  search = '';
  payments: TransferPayment[] = [];
  operations: CashOperation[] = [];
  loading = false;
  errorMessage = '';
  readonly context;

  constructor(private transfertService: Transfert) {
    this.context = this.transfertService.getAgentContext();
  }

  ngOnInit(): void {
    this.loadHistory();
  }

  get filteredPayments(): TransferPayment[] {
    const term = this.search.trim().toLowerCase();
    if (!term) {
      return this.payments;
    }
    return this.payments.filter((payment) =>
      [payment.referenceCode, payment.beneficiaryName, payment.receiptNumber]
        .some((value) => value?.toLowerCase().includes(term)),
    );
  }

  get filteredOperations(): CashOperation[] {
    const term = this.search.trim().toLowerCase();
    if (!term) {
      return this.operations;
    }
    return this.operations.filter((operation) =>
      [operation.reference, operation.operationType]
        .some((value) => value?.toLowerCase().includes(term)),
    );
  }

  operationLabel(operation: CashOperation): string {
    const labels: Record<string, string> = {
      OPENING: 'Ouverture',
      TRANSFER_SENT: 'Envoi',
      TRANSFER_PAID: 'Retrait paye',
      ADJUSTMENT: 'Ajustement',
      CLOSING: 'Cloture',
    };
    return labels[operation.operationType] || operation.operationType;
  }

  loadHistory(): void {
    this.loading = true;
    this.errorMessage = '';

    this.transfertService.getPaymentHistory(this.context.agentId).subscribe({
      next: (payments) => {
        this.payments = payments.sort((a, b) => new Date(b.paidAt).getTime() - new Date(a.paidAt).getTime());
        this.loadOperations();
      },
      error: () => {
        this.payments = [];
        this.loadOperations();
      },
    });
  }

  private loadOperations(): void {
    this.transfertService.getAgentOperations(this.context.agentId).subscribe({
      next: (operations) => {
        this.operations = operations.sort((a, b) => new Date(b.operationDate).getTime() - new Date(a.operationDate).getTime());
        this.loading = false;
      },
      error: () => {
        this.operations = [];
        this.errorMessage = 'Impossible de charger l historique.';
        this.loading = false;
      },
    });
  }
}
