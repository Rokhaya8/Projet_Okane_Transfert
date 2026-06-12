import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { ManagerService } from '../../services/manager.service';
import { DashboardStats } from '../../models/dashboard.model';
import { Operation } from '../../models/operation.model';
import { AgentPerformance } from '../../models/report.model';
import { BadgeStatut } from '../../../../shared/components/badge-statut/badge-statut';
import { EmptyState } from '../../../../shared/components/empty-state/empty-state';
import { formatAmount, formatDate } from '../../../../shared/utils/format.utils';

@Component({
  selector: 'app-manager-dashboard',
  imports: [
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
    BadgeStatut,
    EmptyState,
  ],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css', '../../styles/manager-shared.css'],
})
export class Dashboard implements OnInit {
  private readonly managerService = inject(ManagerService);

  loading = true;
  error = '';
  stats: DashboardStats | null = null;
  recentOps: Operation[] = [];
  agentPerf: AgentPerformance[] = [];
  transferEvolution: { label: string; count: number }[] = [];

  readonly displayedColumns = [
    'referenceCode',
    'agentName',
    'senderName',
    'beneficiaryName',
    'amountSent',
    'sentCurrencyCode',
    'amountReceived',
    'receivedCurrencyCode',
    'createdAt',
    'status',
    'actions',
  ];

  readonly formatAmount = formatAmount;
  readonly formatDate = formatDate;

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.error = '';

    this.managerService.getDashboardStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.loadSecondary();
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Erreur lors du chargement du dashboard';
        this.loading = false;
      },
    });
  }

  private loadSecondary(): void {
    let pending = 3;
    const done = () => {
      pending--;
      if (pending === 0) this.loading = false;
    };

    this.managerService.getRecentOperations().subscribe({
      next: (ops) => {
        this.recentOps = ops;
        done();
      },
      error: () => done(),
    });

    this.managerService.getAgentPerformanceChart().subscribe({
      next: (data) => {
        this.agentPerf = data;
        done();
      },
      error: () => done(),
    });

    this.managerService.getTransferEvolution().subscribe({
      next: (data) => {
        this.transferEvolution = data;
        done();
      },
      error: () => done(),
    });
  }

  get maxAgentOps(): number {
    return Math.max(...this.agentPerf.map((a) => a.totalOperations), 1);
  }

  get maxTransferCount(): number {
    return Math.max(...this.transferEvolution.map((t) => t.count), 1);
  }
}
