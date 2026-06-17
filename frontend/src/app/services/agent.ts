import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AgentService {
  private baseUrl = environment.apiUrl;
  private http = inject(HttpClient);
  protected agentData = signal<any>(null);

  getAgentProfile(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/agent/profile/${id}`);
  }

  getReceptionCountries(sourceCountry: string) {
    return this.http.get<any[]>(
      `${this.baseUrl}/agent/reception-countries`,
      { params: { sourceCountry } }
    );
  }

  setAgentData(data: any): void {
    this.agentData.set(data);
  }
}
