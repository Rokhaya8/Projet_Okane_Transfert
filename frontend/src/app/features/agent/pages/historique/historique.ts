import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar';
import { AgentPayoutService, TransferHistoryDTO } from '../../../../core/services/agent-payout.service';

@Component({
  selector: 'app-historique',
  imports: [FormsModule, CommonModule, DatePipe, NavbarComponent],
  templateUrl: './historique.html',
  styleUrl: './historique.css',
})
export class Historique implements OnInit {
  items: TransferHistoryDTO[] = [];
  totalElements = 0;
  totalPages = 0;
  page = 0;
  size = 10;
  loading = false;

  filterStatus = '';
  filterStart = '';
  filterEnd = '';
  filterSearch = '';

  constructor(private svc: AgentPayoutService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.svc.getHistory(
      this.page, this.size,
      this.filterStatus || undefined,
      this.filterStart || undefined,
      this.filterEnd || undefined,
      this.filterSearch || undefined
    ).subscribe({
      next: (p) => {
        this.loading = false;
        this.items = p.content;
        this.totalElements = p.totalElements;
        this.totalPages = p.totalPages;
      },
      error: () => { this.loading = false; }
    });
  }

  applyFilters() { this.page = 0; this.load(); }

  resetFilters() {
    this.filterStatus = '';
    this.filterStart = '';
    this.filterEnd = '';
    this.filterSearch = '';
    this.page = 0;
    this.load();
  }

  goPage(p: number) { this.page = p; this.load(); }

  pages(): number[] { return Array.from({ length: this.totalPages }, (_, i) => i); }

  statutClass(s: string) {
    return s === 'EN_ATTENTE' ? 'pending' : s === 'PAYÉ' ? 'paid' : s === 'ANNULÉ' ? 'cancelled' : 'expired';
  }
}
