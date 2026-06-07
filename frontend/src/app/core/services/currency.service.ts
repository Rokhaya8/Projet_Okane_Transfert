import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CurrencyResponse {
  id: number;
  code: string;
  name: string;
  symbol: string;
  active: boolean;
}

export interface CurrencyRequest {
  code: string;
  name: string;
  symbol: string;
  active: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {

  private baseUrl = 'http://localhost:8080/api/admin/currencies';

  constructor(private http: HttpClient) {}

  getAllCurrencies(): Observable<CurrencyResponse[]> {
    return this.http.get<CurrencyResponse[]>(this.baseUrl);
  }

  getCurrency(id: number): Observable<CurrencyResponse> {
    return this.http.get<CurrencyResponse>(`${this.baseUrl}/${id}`);
  }

  createCurrency(request: CurrencyRequest): Observable<CurrencyResponse> {
    return this.http.post<CurrencyResponse>(this.baseUrl, request);
  }

  updateCurrency(id: number, request: CurrencyRequest): Observable<CurrencyResponse> {
    return this.http.put<CurrencyResponse>(`${this.baseUrl}/${id}`, request);
  }

  deleteCurrency(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}