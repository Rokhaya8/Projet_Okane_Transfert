import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditLog } from '../models/audit-log.model';

@Injectable({ providedIn: 'root' })
export class AuditLogService {
  private readonly API = 'http://localhost:8080/api/audit-logs';

  constructor(private http: HttpClient) {}

  getAll(): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(this.API);
  }
}