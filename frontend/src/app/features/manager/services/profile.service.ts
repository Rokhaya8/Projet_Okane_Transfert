import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import {
  ChangePasswordRequest,
  ManagerProfile,
  UpdateProfileRequest,
} from '../models/profile.model';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly http = inject(HttpClient);
  private readonly base = `${API_BASE_URL}/manager/profile`;

  getProfile(): Observable<ManagerProfile> {
    return this.http.get<ManagerProfile>(this.base);
  }

  updateProfile(request: UpdateProfileRequest): Observable<ManagerProfile> {
    return this.http.put<ManagerProfile>(this.base, request);
  }

  changePassword(request: ChangePasswordRequest): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.base}/password`, request);
  }
}
