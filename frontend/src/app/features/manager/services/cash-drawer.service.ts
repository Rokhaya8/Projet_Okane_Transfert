import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import { CashDrawer } from '../models/cash-drawer.model';

@Injectable({ providedIn: 'root' })
export class CashDrawerService {
  private readonly http = inject(HttpClient);
  private readonly base = `${API_BASE_URL}/manager/cash-drawers`;

  getCashDrawers(): Observable<CashDrawer[]> {
    return this.http.get<CashDrawer[]>(this.base);
  }
}
