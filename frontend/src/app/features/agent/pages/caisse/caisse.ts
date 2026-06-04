import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar';
import { CaisseService } from '../../../../core/services/caisse.service';

@Component({
  selector: 'app-caisse',
  imports: [FormsModule, CommonModule, DatePipe, NavbarComponent],
  templateUrl: './caisse.html',
  styleUrl: './caisse.css',
})
export class Caisse implements OnInit {
  balance: number = 0;
  operations: any[] = [];
  today = new Date().toISOString().split('T')[0];
  countedAmount: number = 0;
  discrepancyReason = '';
  closeResult: any = null;
  error = '';
  success = '';

  // TODO: récupérer depuis le token JWT
  agentId = 1;

  constructor(private caisseService: CaisseService) {}

  ngOnInit() {
    this.loadBalance();
    this.loadOperations();
  }

  loadBalance() {
    this.caisseService.getBalance(this.agentId).subscribe({
      next: (b) => this.balance = b,
      error: () => this.balance = 0
    });
  }

  loadOperations() {
    this.caisseService.getOperations(this.agentId, this.today).subscribe({
      next: (ops) => this.operations = ops,
      error: () => this.operations = []
    });
  }

  closeCaisse() {
    this.error = '';
    this.caisseService.close(this.agentId, this.countedAmount).subscribe({
      next: (res) => {
        this.closeResult = res;
        this.success = 'Caisse clôturée avec succès';
      },
      error: (err) => this.error = err.error?.message || 'Erreur clôture'
    });
  }

  reportDiscrepancy() {
    if (!this.discrepancyReason) { this.error = 'Motif obligatoire'; return; }
    this.caisseService.reportDiscrepancy(this.agentId, this.countedAmount, this.discrepancyReason).subscribe({
      next: () => this.success = 'Écart signalé avec succès',
      error: () => this.error = 'Erreur lors du signalement'
    });
  }
}
