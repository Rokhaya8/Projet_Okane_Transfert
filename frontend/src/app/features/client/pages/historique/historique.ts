import { Component, OnInit, inject, signal, computed, ChangeDetectorRef } from '@angular/core'; // <-- 1. Importe ChangeDetectorRef
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ClientApiService } from '../../../../core/services/client';
import { ClientStateService } from '../../../../core/services/client-state';
import { Transfer } from '../../../../core/models/client-dashboard.model';

@Component({
  selector: 'app-historique-transfert',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './historique.html',
  styleUrls: ['./historique.css']
})
export class Historique implements OnInit {
  private clientApi = inject(ClientApiService);
  private clientState = inject(ClientStateService);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef); // <-- 2. Injecte-le ici

  filterStatus = signal('ALL');
  searchTerm = signal('');
  currentPage = signal(1);
  readonly pageSize = 5;
  allTransfers = signal<Transfer[]>([]);
  isLoading = true;

  ngOnInit(): void {
    let clientId = this.clientState.currentClient()?.id;

    if (!clientId) {
      let currentRoute: ActivatedRoute | null = this.route;
      let idParam: string | null = null;

      while (currentRoute && !idParam) {
        idParam = currentRoute.snapshot.paramMap.get('clientId');
        currentRoute = currentRoute.parent;
      }

      if (idParam) {
        clientId = +idParam;
      }
    }

    if (clientId) {
      this.isLoading = true;
      // On force une première détection pour afficher le spinner de chargement
      this.cdr.detectChanges(); 

      this.clientApi.getTransferHistory(clientId).subscribe({
        next: (data) => {
          this.allTransfers.set(data ?? []);
          this.isLoading = false;
          
          // <-- 3. LE FIX CRUCIAL ICI : On dit à Angular "Hé, réveille-toi, j'ai les données !"
          this.cdr.detectChanges(); 
        },
        error: (err) => {
          console.error('Erreur historique:', err);
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.isLoading = false;
      this.cdr.detectChanges();
    }
  }

  parseDate(raw: any): Date {
    if (Array.isArray(raw)) return new Date(raw[0], raw[1] - 1, raw[2], raw[3] ?? 0, raw[4] ?? 0);
    return new Date(raw);
  }

  filteredTransfers = computed(() => {
    const status = this.filterStatus();
    const search = this.searchTerm().trim().toLowerCase();
    return this.allTransfers().filter(t => {
      const matchesStatus = status === 'ALL' || t.status === status;
      const matchesSearch = !search ||
        (t.referenceCode && t.referenceCode.toLowerCase().includes(search)) ||
        ((t.beneficiary?.fullName ?? '').toLowerCase().includes(search));
      return matchesStatus && matchesSearch;
    });
  });

  paginatedTransfers = computed(() => {
    const start = (this.currentPage() - 1) * this.pageSize;
    return this.filteredTransfers().slice(start, start + this.pageSize);
  });

  totalPages = computed(() => Math.ceil(this.filteredTransfers().length / this.pageSize) || 1);

  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages()) {
      this.currentPage.set(page);
      this.cdr.detectChanges(); // Optionnel : pour sécuriser le changement de page
    }
  }

  onFilterChange(): void {
    this.currentPage.set(1);
    this.cdr.detectChanges(); // Optionnel : pour sécuriser le filtre
  }
}