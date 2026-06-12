import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { OperationService } from '../../services/operation.service';
import { AgentService } from '../../services/agent.service';
import { Operation, TransferStatus } from '../../models/operation.model';
import { Agent } from '../../models/agent.model';
import { BadgeStatut } from '../../../../shared/components/badge-statut/badge-statut';
import { EmptyState } from '../../../../shared/components/empty-state/empty-state';
import { formatAmount, formatDate } from '../../../../shared/utils/format.utils';

@Component({
  selector: 'app-manager-operations',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSortModule,
    MatTableModule,
    BadgeStatut,
    EmptyState,
  ],
  templateUrl: './operations.html',
  styleUrls: ['./operations.css', '../../styles/manager-shared.css'],
})
export class Operations implements OnInit {
  private readonly operationService = inject(OperationService);
  private readonly agentService = inject(AgentService);
  private readonly fb = inject(FormBuilder);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  loading = true;
  error = '';
  agents: Agent[] = [];
  dataSource = new MatTableDataSource<Operation>([]);

  readonly statuses: TransferStatus[] = ['PENDING', 'PAID', 'CANCELLED', 'EXPIRED'];
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

  readonly filterForm = this.fb.group({
    from: [''],
    to: [''],
    status: ['' as TransferStatus | ''],
    agentId: [null as number | null],
  });

  ngOnInit(): void {
    this.agentService.getAgents().subscribe((agents) => (this.agents = agents));
    this.loadOperations();
  }

  loadOperations(): void {
    this.loading = true;
    this.error = '';
    const f = this.filterForm.getRawValue();

    this.operationService
      .getOperations({
        from: f.from || undefined,
        to: f.to || undefined,
        status: f.status || undefined,
      })
      .subscribe({
        next: (ops) => {
          let filtered = ops;
          if (f.agentId) {
            filtered = ops.filter((o) => o.agentId === f.agentId);
          }
          this.dataSource.data = filtered;
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
          this.loading = false;
        },
        error: (err) => {
          this.error = err?.error?.message ?? 'Erreur lors du chargement';
          this.loading = false;
        },
      });
  }
}
