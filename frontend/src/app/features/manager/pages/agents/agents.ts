import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AgentService } from '../../services/agent.service';
import { ReportService } from '../../services/report.service';
import { Agent } from '../../models/agent.model';
import { AgentPerformance } from '../../models/report.model';
import { BadgeStatut } from '../../../../shared/components/badge-statut/badge-statut';
import { EmptyState } from '../../../../shared/components/empty-state/empty-state';
import { ConfirmDialog } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { formatDate } from '../../../../shared/utils/format.utils';

@Component({
  selector: 'app-manager-agents',
  imports: [
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSortModule,
    MatTableModule,
    MatSnackBarModule,
    BadgeStatut,
    EmptyState,
  ],
  templateUrl: './agents.html',
  styleUrls: ['./agents.css', '../../styles/manager-shared.css'],
})
export class Agents implements OnInit {
  private readonly agentService = inject(AgentService);
  private readonly reportService = inject(ReportService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  loading = true;
  error = '';
  dataSource = new MatTableDataSource<Agent>([]);
  perfMap = new Map<number, number>();

  readonly displayedColumns = [
    'fullName',
    'email',
    'phone',
    'active',
    'totalTransfers',
    'lastLogin',
    'actions',
  ];
  readonly formatDate = formatDate;

  ngOnInit(): void {
    this.loadAgents();
  }

  loadAgents(): void {
    this.loading = true;
    this.error = '';

    this.agentService.getAgents().subscribe({
      next: (agents) => {
        this.reportService.getAgentPerformance().subscribe({
          next: (perf) => {
            perf.forEach((p) => this.perfMap.set(p.agentId, p.totalOperations));
            agents.forEach((a) => (a.totalTransfers = this.perfMap.get(a.id)));
            this.dataSource.data = agents;
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
            this.loading = false;
          },
          error: () => {
            this.dataSource.data = agents;
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
            this.loading = false;
          },
        });
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Erreur lors du chargement des agents';
        this.loading = false;
      },
    });
  }

  applyFilter(event: Event): void {
    const value = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.dataSource.filter = value;
  }

  toggleStatus(agent: Agent): void {
    const action = agent.active ? 'suspendre' : 'activer';
    const dialogRef = this.dialog.open(ConfirmDialog, {
      data: {
        title: agent.active ? 'Suspendre l\'agent' : 'Activer l\'agent',
        message: `Voulez-vous ${action} ${agent.fullName} ?`,
        confirmLabel: agent.active ? 'Suspendre' : 'Activer',
        confirmColor: agent.active ? 'warn' : 'primary',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;
      const req = agent.active
        ? this.agentService.suspendAgent(agent.id)
        : this.agentService.activateAgent(agent.id);
      req.subscribe({
        next: () => {
          this.snackBar.open(`Agent ${action} avec succès`, 'OK', { duration: 3000 });
          this.loadAgents();
        },
        error: (err) =>
          this.snackBar.open(err?.error?.message ?? 'Erreur', 'Fermer', { duration: 4000 }),
      });
    });
  }
}
