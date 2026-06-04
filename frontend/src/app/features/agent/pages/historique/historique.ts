import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar';
import { TransfertService } from '../../../../core/services/transfert';

@Component({
  selector: 'app-historique',
  imports: [FormsModule, CommonModule, DatePipe, NavbarComponent],
  templateUrl: './historique.html',
  styleUrl: './historique.css',
})
export class Historique implements OnInit {
  transfers: any[] = [];
  totalPages = 0;
  currentPage = 0;
  pageSize = 20;

  filters = { status: '', startDate: '', endDate: '' };

  // TODO: récupérer depuis le token JWT
  agentId = 1;

  constructor(private transfertService: TransfertService) {}

  ngOnInit() { this.load(); }

  load() {
    this.transfertService.history(
      this.agentId, this.currentPage, this.pageSize,
      this.filters.status, this.filters.startDate, this.filters.endDate
    ).subscribe({
      next: (res) => {
        this.transfers = res.content || [];
        this.totalPages = res.totalPages || 0;
      },
      error: () => this.transfers = []
    });
  }

  search() { this.currentPage = 0; this.load(); }

  reset() { this.filters = { status: '', startDate: '', endDate: '' }; this.load(); }

  prevPage() { if (this.currentPage > 0) { this.currentPage--; this.load(); } }

  nextPage() { if (this.currentPage < this.totalPages - 1) { this.currentPage++; this.load(); } }
}
