import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

const API = 'http://localhost:8080/okane/api/agent/transfers';

@Injectable({ providedIn: 'root' })
export class TransfertService {

  constructor(private http: HttpClient) {}

  search(code?: string, telephone?: string): Observable<any> {
    let params = new HttpParams();
    if (code) params = params.set('code', code);
    if (telephone) params = params.set('telephoneBeneficiaire', telephone);
    return this.http.get(`${API}/search`, { params });
  }

  payout(transfertId: number, agentId: number, identityNumber: string, identityType: string): Observable<any> {
    return this.http.post(`${API}/${transfertId}/payout?agentId=${agentId}`,
      { identityNumber, identityType });
  }

  history(agentId: number, page = 0, size = 20, status?: string, startDate?: string, endDate?: string): Observable<any> {
    let params = new HttpParams()
      .set('agentId', agentId)
      .set('page', page)
      .set('size', size);
    if (status) params = params.set('status', status);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.get(`${API}/history`, { params });
  }

  getAgentTransfers(agentId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/agent/${agentId}`);
  }
}
