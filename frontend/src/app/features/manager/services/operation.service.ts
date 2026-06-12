import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import { Operation, OperationDetail, OperationFilters } from '../models/operation.model';

@Injectable({ providedIn: 'root' })
export class OperationService {
  private readonly http = inject(HttpClient);
  private readonly base = `${API_BASE_URL}/manager/operations`;

  getOperations(filters?: OperationFilters): Observable<Operation[]> {
    let params = new HttpParams();
    if (filters?.status) params = params.set('status', filters.status);
    if (filters?.from) params = params.set('from', filters.from);
    if (filters?.to) params = params.set('to', filters.to);
    return this.http.get<Operation[]>(this.base, { params });
  }

  getOperation(id: number): Observable<OperationDetail> {
    return this.http.get<OperationDetail>(`${this.base}/${id}`);
  }
}
