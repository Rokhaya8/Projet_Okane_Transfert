import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CorridorResponse {
  id: number;
  sourceCountry: string;
  destinationCountry: string;
  sourceCurrencyId: number;
  sourceCurrencyCode: string;
  sourceCurrencySymbol: string;
  destinationCurrencyId: number;
  destinationCurrencyCode: string;
  destinationCurrencySymbol: string;
  active: boolean;
}

export interface CorridorRequest {
  sourceCountry: string;
  destinationCountry: string;
  sourceCurrencyId: number;
  destinationCurrencyId: number;
  active: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CorridorService {

  private baseUrl = 'http://localhost:8080/api/admin/corridors';

  constructor(private http: HttpClient) {}

  getAllCorridors(): Observable<CorridorResponse[]> {
    return this.http.get<CorridorResponse[]>(this.baseUrl);
  }

  toggleCorridor(id: number): Observable<CorridorResponse> {
    return this.http.patch<CorridorResponse>(`${this.baseUrl}/${id}/toggle`, {});
  }

  createCorridor(request: CorridorRequest): Observable<CorridorResponse> {
    return this.http.post<CorridorResponse>(this.baseUrl, request);
  }

  deleteCorridor(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}