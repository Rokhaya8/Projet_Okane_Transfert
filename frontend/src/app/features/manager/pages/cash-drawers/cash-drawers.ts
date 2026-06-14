import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { CashDrawerService } from '../../services/cash-drawer.service';
import { CashDrawer } from '../../models/cash-drawer.model';
import { BadgeStatut } from '../../../../shared/components/badge-statut/badge-statut';
import { EmptyState } from '../../../../shared/components/empty-state/empty-state';
import { formatAmount, formatDate } from '../../../../shared/utils/format.utils';

@Component({
  selector: 'app-cash-drawers',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSortModule,
    MatTableModule,
    BadgeStatut,
    EmptyState,
  ],
  templateUrl: './cash-drawers.html',
  styleUrls: ['./cash-drawers.css', '../../styles/manager-shared.css'],
})
export class CashDrawers implements OnInit {
  private readonly cashDrawerService = inject(CashDrawerService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  loading = true;
  error = '';
  dataSource = new MatTableDataSource<CashDrawer>([]);

  readonly displayedColumns = [
    'agentName',
    'status',
    'openingBalance',
    'currentBalance',
    'closingBalance',
    'openedAt',
    'closedAt',
  ];
  readonly formatAmount = formatAmount;
  readonly formatDate = formatDate;

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.cashDrawerService.getCashDrawers().subscribe({
      next: (drawers) => {
        this.dataSource.data = drawers;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Erreur lors du chargement des caisses';
        this.loading = false;
      },
    });
  }

  get openCount(): number {
    return this.dataSource.data.filter((d) => d.status === 'OPEN').length;
  }

  get closedCount(): number {
    return this.dataSource.data.filter((d) => d.status === 'CLOSED').length;
  }
}
