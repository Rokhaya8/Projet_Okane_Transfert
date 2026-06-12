import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { ValidationService } from '../../services/validation.service';
import { SensitiveOperation } from '../../models/validation.model';
import { BadgeStatut } from '../../../../shared/components/badge-statut/badge-statut';
import { EmptyState } from '../../../../shared/components/empty-state/empty-state';
import { ConfirmDialog } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { RejectDialog } from '../../../../shared/components/reject-dialog/reject-dialog';
import { formatAmount, formatDate } from '../../../../shared/utils/format.utils';

@Component({
  selector: 'app-manager-validations',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTableModule,
    BadgeStatut,
    EmptyState,
  ],
  templateUrl: './validations.html',
  styleUrls: ['./validations.css', '../../styles/manager-shared.css'],
})
export class Validations implements OnInit {
  private readonly validationService = inject(ValidationService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  loading = true;
  error = '';
  dataSource = new MatTableDataSource<SensitiveOperation>([]);

  readonly displayedColumns = ['id', 'transferReference', 'operationType', 'amount', 'status', 'createdAt', 'actions'];
  readonly formatAmount = formatAmount;
  readonly formatDate = formatDate;

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.validationService.getPendingValidations().subscribe({
      next: (items) => {
        this.dataSource.data = items;
        this.dataSource.paginator = this.paginator;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Erreur lors du chargement';
        this.loading = false;
      },
    });
  }

  approve(item: SensitiveOperation): void {
    const dialogRef = this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Valider l\'opération',
        message: `Confirmer la validation de l'opération #${item.id} ?`,
        confirmLabel: 'Valider',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;
      this.validationService.approve(item.id).subscribe({
        next: () => {
          this.snackBar.open('Opération validée', 'OK', { duration: 3000 });
          this.load();
        },
        error: (err) =>
          this.snackBar.open(err?.error?.message ?? 'Erreur', 'Fermer', { duration: 4000 }),
      });
    });
  }

  reject(item: SensitiveOperation): void {
    const dialogRef = this.dialog.open(RejectDialog, {
      data: {
        title: 'Rejeter l\'opération',
        message: `Indiquez le motif de rejet pour l'opération #${item.id}.`,
      },
    });

    dialogRef.afterClosed().subscribe((reason) => {
      if (!reason) return;
      this.validationService.reject(item.id, { reason }).subscribe({
        next: () => {
          this.snackBar.open('Opération rejetée', 'OK', { duration: 3000 });
          this.load();
        },
        error: (err) =>
          this.snackBar.open(err?.error?.message ?? 'Erreur', 'Fermer', { duration: 4000 }),
      });
    });
  }
}
