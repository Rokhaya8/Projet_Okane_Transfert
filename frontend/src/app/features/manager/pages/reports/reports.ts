import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ReportService } from '../../services/report.service';
import { AgencyPerformance, AgencyReport, AgentPerformance, ReportPeriod } from '../../models/report.model';
import { EmptyState } from '../../../../shared/components/empty-state/empty-state';
import { downloadBlob, formatAmount } from '../../../../shared/utils/format.utils';

@Component({
  selector: 'app-manager-reports',
  imports: [
    DecimalPipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSnackBarModule,
    EmptyState,
  ],
  templateUrl: './reports.html',
  styleUrls: ['./reports.css', '../../styles/manager-shared.css'],
})
export class Reports implements OnInit {
  private readonly reportService = inject(ReportService);
  private readonly fb = inject(FormBuilder);
  private readonly snackBar = inject(MatSnackBar);

  loading = true;
  error = '';
  report: AgencyReport | null = null;
  performance: AgencyPerformance | null = null;
  agentPerf: AgentPerformance[] = [];

  readonly periods: ReportPeriod[] = ['DAY', 'WEEK', 'MONTH'];
  readonly formatAmount = formatAmount;

  readonly filterForm = this.fb.nonNullable.group({
    period: ['DAY' as ReportPeriod],
    from: [''],
    to: [''],
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    const { period, from, to } = this.filterForm.getRawValue();

    this.reportService.getReport(period).subscribe({
      next: (report) => {
        this.report = report;
        this.reportService.getPerformance().subscribe({
          next: (perf) => {
            this.performance = perf;
            this.reportService.getAgentPerformance(from || undefined, to || undefined).subscribe({
              next: (agents) => {
                this.agentPerf = agents;
                this.loading = false;
              },
              error: () => (this.loading = false),
            });
          },
          error: () => (this.loading = false),
        });
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Erreur lors du chargement';
        this.loading = false;
      },
    });
  }

  exportPdf(): void {
    const { from, to } = this.filterForm.getRawValue();
    this.reportService.exportPdf(from || undefined, to || undefined).subscribe({
      next: (blob) => downloadBlob(blob, 'rapport-agence.pdf'),
      error: (err) =>
        this.snackBar.open(err?.error?.message ?? 'Erreur export PDF', 'Fermer', { duration: 4000 }),
    });
  }

  exportExcel(): void {
    const { from, to } = this.filterForm.getRawValue();
    this.reportService.exportExcel(from || undefined, to || undefined).subscribe({
      next: (blob) => downloadBlob(blob, 'rapport-agence.csv'),
      error: (err) =>
        this.snackBar.open(err?.error?.message ?? 'Erreur export Excel', 'Fermer', { duration: 4000 }),
    });
  }

  get maxAgentAmount(): number {
    return Math.max(...this.agentPerf.map((a) => Number(a.totalAmount)), 1);
  }

  getAgentBarHeight(amount: number): number {
    return (Number(amount) / this.maxAgentAmount) * 140;
  }

  get statusDistribution(): { label: string; count: number; pct: number }[] {
    if (!this.report) return [];
    const total = this.report.transactionCount || 1;
    return [
      { label: 'Payés', count: this.report.paidCount, pct: (this.report.paidCount / total) * 100 },
      { label: 'En attente', count: this.report.pendingCount, pct: (this.report.pendingCount / total) * 100 },
      { label: 'Annulés', count: this.report.cancelledCount, pct: (this.report.cancelledCount / total) * 100 },
    ];
  }
}
