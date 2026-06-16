import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import {
  AgencyPerformance,
  AgencyReport,
  AgentPerformance,
  ReportPeriod,
} from '../models/report.model';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly http = inject(HttpClient);
  private readonly base = `${API_BASE_URL}/manager`;

  getReport(period: ReportPeriod): Observable<AgencyReport> {
    const params = new HttpParams().set('period', period);
    return this.http.get<AgencyReport>(`${this.base}/reports`, { params });
  }

  getPerformance(): Observable<AgencyPerformance> {
    return this.http.get<AgencyPerformance>(`${this.base}/performance`);
  }

  getAgentPerformance(from?: string, to?: string): Observable<AgentPerformance[]> {
    let params = new HttpParams();
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    return this.http.get<AgentPerformance[]>(`${this.base}/performance/agents`, { params });
  }

  exportExcel(from?: string, to?: string): Observable<Blob> {
    let params = new HttpParams();
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    return this.http.get(`${this.base}/reports/export/excel`, {
      params,
      responseType: 'blob',
    });
  }

  exportPdf(from?: string, to?: string): Observable<Blob> {
    let params = new HttpParams();
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    return this.http.get(`${this.base}/reports/export/pdf`, {
      params,
      responseType: 'blob',
    });
  }
}
