import { Component, OnInit, OnDestroy, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { Subject, takeUntil, finalize } from 'rxjs';
import { ReportService, ReportData, ReportStats } from '../../../../core/services/report.service';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

const STATUS_COLORS: Record<string, string> = {
  VALIDE:     '#EC6426',
  EN_ATTENTE: '#F8A91F',
  ANNULE:     '#632713',
  COMPLETED:  '#EC6426',
  PENDING:    '#F8A91F',
  CANCELLED:  '#632713',
};

@Component({
  selector: 'app-admin-report',
  standalone: true,
  imports: [CommonModule, DatePipe, DecimalPipe],
  templateUrl: './rapports.html',
  styleUrls: ['./rapports.css']
})
export class AdminReportComponent implements OnInit, OnDestroy {
  private reportService = inject(ReportService);
  private cdr = inject(ChangeDetectorRef);
  private destroy$ = new Subject<void>();

  data: ReportData | null = null;
  stats: ReportStats | null = null;
  loading = false;
  generatingPdf = false;
  error: string | null = null;

  ngOnInit(): void {
    this.loadData();
  }

  ngOnDestroy(): void {
    // Annule proprement le subscribe si le composant est détruit
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadData(): void {
    this.loading = true;
    this.error = null;
    this.data = null;
    this.stats = null;

    this.reportService.loadReportData()
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (data) => {
          console.log('[Rapport] données reçues:', data);
          this.data = data;
          this.stats = this.reportService.computeStats(data);
        },
        error: (err) => {
          console.error('[Rapport] erreur fatale:', err);
          this.error = 'Erreur lors du chargement des données.';
        }
      });
  }

  statusColor(status: string): string {
    return STATUS_COLORS[status] ?? '#888888';
  }

  async generatePDF(): Promise<void> {
    this.generatingPdf = true;
    await new Promise(r => setTimeout(r, 120));

    const el = document.getElementById('report-content')!;
    const canvas = await html2canvas(el, {
      scale: 2,
      useCORS: true,
      backgroundColor: '#ffffff',
      logging: false
    });

    const imgData = canvas.toDataURL('image/png');
    const pdf = new jsPDF('p', 'mm', 'a4');
    const pageW = pdf.internal.pageSize.getWidth();
    const pageH = pdf.internal.pageSize.getHeight();
    const imgH = (canvas.height * pageW) / canvas.width;

    let posY = 0;
    let remaining = imgH;
    while (remaining > 0) {
      pdf.addImage(imgData, 'PNG', 0, posY === 0 ? 0 : -posY, pageW, imgH);
      remaining -= pageH;
      if (remaining > 0) { pdf.addPage(); posY += pageH; }
    }

    pdf.save(`rapport-okanetransfer-${new Date().toISOString().slice(0, 10)}.pdf`);
    this.generatingPdf = false;
  }
}