import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ClientApiService } from '../../../../core/services/client';
import { ClientStateService } from '../../../../core/services/client-state';
import { Transfer } from '../../../../core/models/client-dashboard.model';

@Component({
  selector: 'app-detail-transfert',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detail-transfert.html',
  styleUrl: './detail-transfert.css'
})
export class DetailTransfert {
  private clientApi = inject(ClientApiService);
  private clientState = inject(ClientStateService);
  private route = inject(ActivatedRoute);

  searchReference = '';
  hasSearched = false;
  isLoading = false;
  errorMessage = '';
  transfertTrouve: Transfer | null = null;

  rechercheLeTransfert(): void {
    const refInput = this.searchReference.trim();
    if (!refInput) {
      this.errorMessage = 'Veuillez entrer un code de référence.';
      this.transfertTrouve = null;
      return;
    }
    const clientId = this.clientState.resolveClientId(this.route.snapshot);
    if (!clientId) {
      this.errorMessage = 'Session expirée, veuillez vous reconnecter.';
      return;
    }
    this.isLoading = true;
    this.hasSearched = true;
    this.errorMessage = '';
    this.transfertTrouve = null;

    this.clientApi.trackTransfer(clientId, refInput).subscribe({
      next: (data) => { this.transfertTrouve = data; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }
}
