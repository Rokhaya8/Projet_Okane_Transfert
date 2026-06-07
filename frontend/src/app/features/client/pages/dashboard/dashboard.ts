import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables } from 'chart.js';

// Enregistrement des composants essentiels de Chart.js
Chart.register(...registerables);

interface Transfert {
  reference: string;
  beneficiaire: string;
  montant: number;
  devise: string;
  statut: 'EN_ATTENTE' | 'PAYÉ' | 'ANNULÉ' | 'EXPIRÉ';
  date: string;
}

@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class ClientDashboardComponent implements OnInit, AfterViewInit {
  
  // Informations fictives du client connecté
  clientName: string = 'Zineb Chaouch';
  clientEmail: string = 'zineb.chaouch@email.com';
  
  // Statistiques rapides
  totalEnvoye: number = 14500;
  transfertsActifsCount: number = 1;

  // Liste des transferts récents (Mock Data conformes aux statuts du CDC)
  recentTransferts: Transfert[] = [
    { reference: 'TRX-849201', beneficiaire: 'Ahmed Alami', montant: 4500, devise: 'MAD', statut: 'EN_ATTENTE', date: '06/06/2026' },
    { reference: 'TRX-732104', beneficiaire: 'Marie Dupont', montant: 300, devise: 'EUR', statut: 'PAYÉ', date: '28/05/2026' },
    { reference: 'TRX-610923', beneficiaire: 'John Doe', montant: 500, devise: 'USD', statut: 'PAYÉ', date: '15/05/2026' },
    { reference: 'TRX-504112', beneficiaire: 'Youssef Benani', montant: 2000, devise: 'MAD', statut: 'ANNULÉ', date: '02/05/2026' }
  ];

  // Référence vers l'élément HTML <canvas> pour Chart.js
  @ViewChild('statsChart') statsChartCanvas!: ElementRef;
  chart: any;

  constructor() {}

  ngOnInit(): void {}

  ngAfterViewInit(): void {
    this.initChart();
  }

  // Initialisation du graphique Chart.js requis par le CDC
  initChart() {
    this.chart = new Chart(this.statsChartCanvas.nativeElement, {
      type: 'bar', // Type de graphique (Barres)
      data: {
        labels: ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin'],
        datasets: [{
          label: 'Montant total envoyé (en MAD équivalent)',
          data: [2000, 1500, 4000, 0, 2500, 4500],
          backgroundColor: '#e8541a', // Ton orange dynamique Okane Transfer
          borderRadius: 6,
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false }
        },
        scales: {
          y: { beginAtZero: true }
        }
      }
    });
  }

  // Méthode utilitaire pour attribuer une couleur CSS selon le statut du transfert
  getStatutClass(statut: string): string {
    switch(statut) {
      case 'PAYÉ': return 'badge-success';
      case 'EN_ATTENTE': return 'badge-warning';
      case 'ANNULÉ': return 'badge-danger';
      default: return 'badge-secondary';
    }
  }
}