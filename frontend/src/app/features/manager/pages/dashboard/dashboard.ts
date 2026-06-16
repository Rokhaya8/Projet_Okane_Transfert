import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { ManagerService } from '../../services/manager.service';
import { ReportService } from '../../services/report.service';
import { DashboardStats } from '../../models/dashboard.model';
import { Operation } from '../../models/operation.model';
import { AgentPerformance } from '../../models/report.model';
import { BadgeStatut } from '../../../../shared/components/badge-statut/badge-statut';
import { EmptyState } from '../../../../shared/components/empty-state/empty-state';
import { formatAmount, formatDate } from '../../../../shared/utils/format.utils';

@Component({
  selector: 'app-manager-dashboard',
  imports: [RouterLink, MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatTableModule, BadgeStatut, EmptyState],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css', '../../styles/manager-shared.css'],
})
export class Dashboard implements OnInit {
  private readonly managerService = inject(ManagerService);
  private readonly reportService = inject(ReportService);

  loading = true;
  error = '';
  stats: DashboardStats | null = null;
  recentOps: Operation[] = [];
  agentPerf: AgentPerformance[] = [];
  transferEvolution: { label: string; count: number }[] = [];

  readonly displayedColumns = ['referenceCode','agentName','senderName','beneficiaryName','amountSent','sentCurrencyCode','amountReceived','receivedCurrencyCode','createdAt','status','actions'];
  readonly formatAmount = formatAmount;
  readonly formatDate = formatDate;

  ngOnInit(): void { this.loadData(); }

  loadData(): void {
    this.loading = true;
    this.error = '';
    forkJoin({
      stats:             this.managerService.getDashboardStats()       .pipe(catchError(() => of(null))),
      recentOps:         this.managerService.getRecentOperations()     .pipe(catchError(() => of([]))),
      agentPerf:         this.reportService.getAgentPerformance()      .pipe(catchError(() => of([]))),
      transferEvolution: this.managerService.getTransferEvolution()    .pipe(catchError(() => of([]))),
    }).subscribe({
      next: ({ stats, recentOps, agentPerf, transferEvolution }) => {
        this.stats             = stats as DashboardStats | null;
        this.recentOps         = recentOps as Operation[];
        this.agentPerf         = agentPerf as AgentPerformance[];
        this.transferEvolution = transferEvolution as { label: string; count: number }[];
        this.loading = false;
      },
      error: () => {
        this.error = 'Erreur lors du chargement du dashboard';
        this.loading = false;
      },
    });
  }

  get maxAgentOps(): number { return Math.max(...this.agentPerf.map((a) => a.totalOperations), 1); }
  get maxTransferCount(): number { return Math.max(...this.transferEvolution.map((t) => t.count), 1); }
}
