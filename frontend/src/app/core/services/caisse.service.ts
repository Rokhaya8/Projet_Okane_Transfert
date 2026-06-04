import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

const API = 'http://localhost:8080/okane/api/agent/cash';

@Injectable({ providedIn: 'root' })
export class CaisseService {

  constructor(private http: HttpClient) {}

  getBalance(agentId: number): Observable<number> {
    return this.http.get<number>(`${API}/balance?agentId=${agentId}`);
  }

  getOperations(agentId: number, date: string): Observable<any[]> {
    return this.http.get<any[]>(`${API}/operations?agentId=${agentId}&date=${date}`);
  }

  close(agentId: number, countedAmount: number): Observable<any> {
    return this.http.post(`${API}/close?agentId=${agentId}&countedAmount=${countedAmount}`, {});
  }

  reportDiscrepancy(agentId: number, countedAmount: number, reason: string): Observable<any> {
    return this.http.post(`${API}/discrepancy?agentId=${agentId}`,
      { countedAmount, reason });
  }
}
