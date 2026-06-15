import { Component, OnInit, ViewChild, ElementRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { ClientApiService } from '../../../../core/services/client';
import { ClientStateService } from '../../../../core/services/client-state';
import { ClientProfile, Transfer } from '../../../../core/models/client-dashboard.model';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class ClientDashboardComponent implements OnInit {
  private clientApi = inject(ClientApiService);
  private clientState = inject(ClientStateService);
  private route = inject(ActivatedRoute);

  currentClientId!: number;
  totalEnvoye = 0;
  transfertsActifsCount = 0;
  recentTransferts: Transfer[] = [];

  get clientName(): string { return this.clientState.currentClient()?.fullName ?? ''; }
  get clientEmail(): string { return this.clientState.currentClient()?.email ?? ''; }

  @ViewChild('statsChart') statsChartCanvas!: ElementRef;
  private chart: any;

  ngOnInit(): void {
    const id = this.clientState.resolveClientId(this.route.snapshot);
    if (id) {
      this.currentClientId = id;
      this.loadDashboardData();
    }
  }

  loadDashboardData(): void {
    this.clientApi.getDashboardData(this.currentClientId).subscribe({
      next: (data) => {
        this.totalEnvoye = data.totalMoneySent;
        this.transfertsActifsCount = data.pendingTransfersCount;
        this.recentTransferts = data.recentTransfers;

        const clientInfo = data.recentTransfers[0]?.client;
        if (clientInfo) {
          this.clientState.currentClient.set(clientInfo as ClientProfile);
        }

        this.initChart();
      },
      error: (err) => console.error('Erreur dashboard:', err)
    });
  }

  private initChart(): void {
    if (this.chart) this.chart.destroy();

    const montants = this.recentTransferts.map(t => t.amountSent).reverse();
    const labels = this.recentTransferts.map(t => t.referenceCode).reverse();

    this.chart = new Chart(this.statsChartCanvas.nativeElement, {
      type: 'bar',
      data: {
        labels: labels.length > 0 ? labels : ['Aucun transfert'],
        datasets: [{
          label: 'Montant de la transaction (MAD)',
          data: montants.length > 0 ? montants : [0],
          backgroundColor: '#e8541a',
          borderRadius: 6,
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true } }
      }
    });
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'PAID': return 'badge-success';
      case 'PENDING': return 'badge-warning';
      case 'CANCELLED': return 'badge-danger';
      default: return 'badge-secondary';
    }
  }

  getStatutLabel(statut: string): string {
    switch (statut) {
      case 'PAID': return 'PAYÉ';
      case 'PENDING': return 'EN ATTENTE';
      case 'CANCELLED': return 'ANNULÉ';
      default: return statut;
    }
  }
}
