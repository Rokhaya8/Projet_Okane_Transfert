import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root' // Indispensable pour que le service soit utilisable partout
})
export class AgentService {
  // L'URL de base qui pointe vers ton backend Java
  private baseUrl = 'http://localhost:8080/api';

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
