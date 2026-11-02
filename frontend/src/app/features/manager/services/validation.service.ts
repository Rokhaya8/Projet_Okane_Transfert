import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import { RejectValidationRequest, SensitiveOperation } from '../models/validation.model';

@Injectable({ providedIn: 'root' })
export class ValidationService {
  private readonly http = inject(HttpClient);
  private readonly base = `${API_BASE_URL}/manager/validations`;

  getPendingValidations(): Observable<SensitiveOperation[]> {
    return this.http.get<SensitiveOperation[]>(this.base);
  }

  approve(id: number): Observable<SensitiveOperation> {
    return this.http.post<SensitiveOperation>(`${this.base}/${id}/approve`, {});
  }

  reject(id: number, request: RejectValidationRequest): Observable<SensitiveOperation> {
    return this.http.post<SensitiveOperation>(`${this.base}/${id}/reject`, request);
  }
}
