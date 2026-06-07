import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AgencyManager {
  id: number;
  username: string;
  email: string;
}

export interface Agence {
  id: number;
  name: string;
  address: string;
  country: string;
  dailyLimit: number;
  active: boolean;
  createdAt: string;
  manager?: AgencyManager;
  managerName?: string;
}

export interface AgenceRequest {
  name: string;
  address: string;
  country: string;
  dailyLimit: number;
  active: boolean;
  managerId?: number;
}

@Injectable({
  providedIn: 'root',
})
export class AgenceService {
  private readonly apiUrl = 'http://localhost:8080/api/admin/agencies';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Agence[]> {
    return this.http.get<Agence[]>(this.apiUrl);
  }

  creer(data: AgenceRequest): Observable<HttpResponse<Agence>> {
    return this.http.post<Agence>(this.apiUrl, data, { observe: 'response' });
  }

  modifier(id: number, data: AgenceRequest): Observable<HttpResponse<Agence>> {
    return this.http.put<Agence>(`${this.apiUrl}/${id}`, data, { observe: 'response' });
  }

  supprimer(id: number): Observable<HttpResponse<void>> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { observe: 'response' });
  }
}