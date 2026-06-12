import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { OperationService } from '../../services/operation.service';
import { OperationDetail } from '../../models/operation.model';
import { BadgeStatut } from '../../../../shared/components/badge-statut/badge-statut';
import { formatAmount, formatDate } from '../../../../shared/utils/format.utils';

@Component({
  selector: 'app-operation-detail',
  imports: [
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    BadgeStatut,
  ],
  templateUrl: './operation-detail.html',
  styleUrls: ['./operation-detail.css', '../../styles/manager-shared.css'],
})
export class OperationDetailPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly operationService = inject(OperationService);

  operation: OperationDetail | null = null;
  loading = true;
  error = '';

  readonly formatAmount = formatAmount;
  readonly formatDate = formatDate;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.operationService.getOperation(id).subscribe({
      next: (op) => {
        this.operation = op;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Opération introuvable';
        this.loading = false;
      },
    });
  }
}
