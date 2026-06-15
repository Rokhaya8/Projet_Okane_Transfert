import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of, combineLatest } from 'rxjs';

// ─── INTERFACES ────────────────────────────────────────────────

export interface AgencyResponse {
  id: number;
  name: string;
  address?: string;
  country?: string;
  dailyLimit?: number;
  active: boolean;
  createdAt?: string;
  managerName?: string | null;
}

export interface CurrencyResponse {
  id: number;
  code: string;
  name: string;
  symbol?: string;
  active: boolean;
}

export interface TransferResponse {
  id: number;
  amount: number;
  status: string;
  createdAt: string;
  senderName?: string;
  receiverName?: string;
  sourceCurrency?: string;
  targetCurrency?: string;
  agencyName?: string;
}

export interface TransferCorridorResponse {
  id: number;
  sourceCountry: string;
  destinationCountry: string;
  sourceCurrencyId: number;
  destinationCurrencyId: number;
  sourceCurrencyCode: string;
  sourceCurrencySymbol?: string;
  destinationCurrencyCode: string;
  destinationCurrencySymbol?: string;
  sourceCurrency?: string;
  targetCurrency?: string;
  active: boolean;
  exchangeRate?: number;
  fees?: number;
}

export interface ReportData {
  agencies: AgencyResponse[];
  currencies: CurrencyResponse[];
  transfers: TransferResponse[];
  corridors: TransferCorridorResponse[];
  generatedAt: Date;
}

export interface ReportStats {
  totalTransfers: number;
  totalAmount: number;
  activeAgencies: number;
  totalAgencies: number;
  activeCorridors: number;
  totalCorridors: number;
  activeCurrencies: number;
  transfersByStatus: { status: string; count: number; percentage: number }[];
}

// ─── SERVICE ───────────────────────────────────────────────────

@Injectable({ providedIn: 'root' })
export class ReportService {
  private http = inject(HttpClient);

  private readonly BASE = 'http://localhost:8080/api';

  /**
   * Charge toutes les données en parallèle avec combineLatest.
   * Chaque requête a un catchError individuel → une erreur
   * sur un endpoint n'annule PAS les autres.
   */
  loadReportData(): Observable<ReportData> {
    const safe = <T>(obs: Observable<T[]>): Observable<T[]> =>
      obs.pipe(catchError(err => {
        console.warn('Endpoint error (ignoré):', err?.url, err?.status);
        return of([] as T[]);
      }));

    return combineLatest({
      agencies:   safe(this.http.get<AgencyResponse[]>(`${this.BASE}/admin/agencies`)),
      currencies: safe(this.http.get<CurrencyResponse[]>(`${this.BASE}/admin/currencies`)),
      transfers:  safe(this.http.get<TransferResponse[]>(`${this.BASE}/transfers`)),
      corridors:  safe(this.http.get<TransferCorridorResponse[]>(`${this.BASE}/admin/corridors`)),
    }).pipe(
      map(data => ({ ...data, generatedAt: new Date() }))
    );
  }

  /**
   * Calcule les statistiques globales depuis les données brutes
   */
  computeStats(data: ReportData): ReportStats {
    const statusMap = new Map<string, number>();
    data.transfers.forEach(t =>
      statusMap.set(t.status, (statusMap.get(t.status) || 0) + 1)
    );
    const total = data.transfers.length;

    return {
      totalTransfers:    total,
      totalAmount:       data.transfers.reduce((s, t) => s + (t.amount || 0), 0),
      activeAgencies:    data.agencies.filter(a => a.active).length,
      totalAgencies:     data.agencies.length,
      activeCorridors:   data.corridors.filter(c => c.active).length,
      totalCorridors:    data.corridors.length,
      activeCurrencies:  data.currencies.filter(c => c.active).length,
      transfersByStatus: Array.from(statusMap.entries()).map(([status, count]) => ({
        status,
        count,
        percentage: total > 0 ? Math.round((count / total) * 100) : 0
      }))
    };
  }

  /** Accès individuel réutilisable */
  getAgencies()   { return this.http.get<AgencyResponse[]>(`${this.BASE}/admin/agencies`); }
  getCurrencies() { return this.http.get<CurrencyResponse[]>(`${this.BASE}/admin/currencies`); }
  getTransfers()  { return this.http.get<TransferResponse[]>(`${this.BASE}/transfers`); }
  
  getCorridors()  { return this.http.get<TransferCorridorResponse[]>(`${this.BASE}/admin/corridors`); }
}