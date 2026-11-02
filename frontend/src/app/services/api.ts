import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = environment.apiUrl;
  private http = inject(HttpClient);
  protected agentData = signal<any>(null);

  getAgentProfile(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/agent/profile/${id}`);
  }

  setAgentData(data: any): void {
    this.agentData.set(data);
  }
}
