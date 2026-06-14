import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClientDashboardInfo, Transfer } from '../models/client-dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class ClientApiService {
  private baseUrl = 'http://localhost:8080/api/client';
  private http = inject(HttpClient);

  // 1. Récupère les infos typées du Dashboard
  getDashboardData(clientId: number): Observable<ClientDashboardInfo> {
    return this.http.get<ClientDashboardInfo>(`${this.baseUrl}/${clientId}/dashboard`, { withCredentials: true });
  }

  // 2. Récupère l'historique sous forme de tableau de transferts
  getTransferHistory(clientId: number): Observable<Transfer[]> {
    return this.http.get<Transfer[]>(`${this.baseUrl}/${clientId}/history`, { withCredentials: true });
  }

  // 3. Traque un transfert spécifique
  trackTransfer(clientId: number, referenceCode: string): Observable<Transfer> {
    return this.http.get<Transfer>(`${this.baseUrl}/${clientId}/track/${referenceCode}`, { withCredentials: true });
  }
}