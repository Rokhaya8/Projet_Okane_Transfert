import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TransferResponse } from '../models/model';

@Injectable({ providedIn: 'root' })
export class TransferService {
  private base = 'http://localhost:8080/api/transfers';

  constructor(private http: HttpClient) {}

  getAll(): Observable<TransferResponse[]> {
    return this.http.get<TransferResponse[]>(this.base);
  }
}
