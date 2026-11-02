import { Component, OnInit, AfterViewInit, ElementRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TransferService } from '../../../../core/services/transfert.service';
import { TransferResponse } from '../../../../core/models/model';
import { MatIconModule } from '@angular/material/icon';
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, MatIconModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit, AfterViewInit {
  @ViewChild('barCanvas') barCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('donutCanvas') donutCanvas!: ElementRef<HTMLCanvasElement>;
  
  transfers: TransferResponse[] = [];
  recentTransfers: TransferResponse[] = [];
  loading = true;
  errorMessage: string | null = null;

  get todayCount(): number {
    const today = new Date().toDateString();
    return this.transfers.filter(t => new Date(t.createdAt).toDateString() === today).length;
  }

  get totalVolumeLabel(): string {
    const sum = this.transfers.reduce((s, t) => s + Number(t.amountSent), 0);
    if (sum >= 1_000_000) return (sum / 1_000_000).toFixed(1) + 'M MAD';
    if (sum >= 1_000) return (sum / 1_000).toFixed(0) + 'k MAD';
    return sum + ' MAD';
  }

  get totalFeesLabel(): string {
    const sum = this.transfers.reduce((s, t) => s + Number(t.fees), 0);
    return new Intl.NumberFormat('fr-MA').format(sum) + ' MAD';
  }

  get paidCount(): number { return this.transfers.filter(t => t.status === 'PAID').length; }
  get pendingCount(): number { return this.transfers.filter(t => t.status === 'PENDING').length; }

  get last30Days(): { day: string; total: number }[] {
    const days: Record<string, number> = {};
    const now = new Date();
    for (let i = 29; i >= 0; i--) {
      const d = new Date(now); d.setDate(now.getDate() - i);
      days[d.toDateString()] = 0;
    }
    this.transfers.forEach(t => {
      const key = new Date(t.createdAt).toDateString();
      if (key in days) days[key] += Number(t.amountSent);
    });
    return Object.entries(days).map(([k, v]) => ({
      day: new Date(k).getDate().toString().padStart(2, '0'), total: v
    }));
  }

  get agencyStats(): { label: string; total: number; pct: number; color: string }[] {
    const colors = ['#EC6426', '#F8A91F', '#632713', '#a0522d', '#FDE3CF'];
    const map: Record<string, number> = {};
    this.transfers.forEach(t => { const k = t.agencyName || 'Autre'; map[k] = (map[k] || 0) + Number(t.amountSent); });
    const sorted = Object.entries(map).sort((a, b) => b[1] - a[1]);
    const top4 = sorted.slice(0, 4);
    const others = sorted.slice(4).reduce((s, [, v]) => s + v, 0);
    const all = others > 0 ? [...top4, ['Autres', others] as [string, number]] : top4;
    const total = all.reduce((s, [, v]) => s + v, 0) || 1;
    return all.map(([label, val], i) => ({
      label, total: val, pct: Math.round((val / total) * 100), color: colors[i] ?? colors[4]
    }));
  }

  constructor(private transferService: TransferService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.transferService.getAll().subscribe({
      next: data => {
        console.log('Transfer data', data);
        try {
          console.log('Transfer data raw', JSON.stringify(data, null, 2));
        } catch (e) {
          console.warn('Could not stringify transfer data', e);
        }
        this.transfers = data;
        this.recentTransfers = [...data]
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
          .slice(0, 10);
        this.loading = false;
        this.cdr.detectChanges();
        setTimeout(() => this.drawCharts(), 100);
      },
      error: err => {
        console.error('Erreur API transfers', err);
        this.errorMessage = 'Impossible de charger les transferts depuis le backend.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  ngAfterViewInit(): void {}

  drawCharts(): void { this.drawBarChart(); this.drawDonutChart(); }

  drawBarChart(): void {
    const canvas = this.barCanvas?.nativeElement;
    if (!canvas) return;
    const ctx = canvas.getContext('2d')!;
    const data = this.last30Days;
    const max = Math.max(...data.map(d => d.total), 140000);
    const W = canvas.width, H = canvas.height;
    const padL = 52, padR = 12, padT = 16, padB = 40;
    const chartW = W - padL - padR, chartH = H - padT - padB;
    const gap = chartW / data.length;
    const barW = gap * 0.6;

    ctx.clearRect(0, 0, W, H);
    ctx.font = '11px Inter, sans-serif';

    [0, 35000, 70000, 105000, 140000].forEach(tick => {
      const y = padT + chartH - (tick / max) * chartH;
      ctx.beginPath(); ctx.strokeStyle = 'rgba(99,39,19,0.08)'; ctx.lineWidth = 1;
      ctx.moveTo(padL, y); ctx.lineTo(W - padR, y); ctx.stroke();
      ctx.fillStyle = '#b0856a'; ctx.textAlign = 'right';
      ctx.fillText(tick === 0 ? '0k' : (tick / 1000) + 'k', padL - 6, y + 4);
    });

    data.forEach((d, i) => {
      const x = padL + i * gap + (gap - barW) / 2;
      const barH = d.total > 0 ? Math.max((d.total / max) * chartH, 3) : 2;
      const y = padT + chartH - barH;
      const grad = ctx.createLinearGradient(0, y, 0, y + barH);
      grad.addColorStop(0, '#EC6426'); grad.addColorStop(1, '#f8a080');
      ctx.fillStyle = grad;
      ctx.beginPath();
      (ctx as any).roundRect ? (ctx as any).roundRect(x, y, barW, barH, [3, 3, 0, 0]) : ctx.rect(x, y, barW, barH);
      ctx.fill();
      if (i % 5 === 0) {
        ctx.fillStyle = '#b0856a'; ctx.textAlign = 'center';
        ctx.fillText(d.day, x + barW / 2, padT + chartH + 18);
      }
    });
  }

  drawDonutChart(): void {
    const canvas = this.donutCanvas?.nativeElement;
    if (!canvas) return;
    const ctx = canvas.getContext('2d')!;
    const stats = this.agencyStats;
    const cx = canvas.width / 2, cy = canvas.height / 2;
    const R = Math.min(cx, cy) - 8, r = R * 0.56;
    let angle = -Math.PI / 2;

    ctx.clearRect(0, 0, canvas.width, canvas.height);
    stats.forEach(s => {
      const slice = (s.pct / 100) * 2 * Math.PI;
      ctx.beginPath(); ctx.moveTo(cx, cy); ctx.arc(cx, cy, R, angle, angle + slice);
      ctx.closePath(); ctx.fillStyle = s.color; ctx.fill();
      angle += slice;
    });
    ctx.beginPath(); ctx.arc(cx, cy, r, 0, 2 * Math.PI);
    ctx.fillStyle = '#fff'; ctx.fill();
  }

  statusLabel(s: string): string {
    return ({ PAID: 'Payé', PENDING: 'En attente', CANCELLED: 'Annulé', PROCESSING: 'En cours' } as any)[s] || s;
  }
  statusClass(s: string): string {
    return ({ PAID: 'badge-paid', PENDING: 'badge-pending', CANCELLED: 'badge-cancelled', PROCESSING: 'badge-processing' } as any)[s] || '';
  }
  fmt(n: number): string { return new Intl.NumberFormat('fr-MA').format(n); }
  fmtDate(d: string): string { return new Date(d).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' }); }
}