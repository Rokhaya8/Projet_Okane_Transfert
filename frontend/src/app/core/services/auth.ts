import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { AuthTokenService } from './auth-token.service';
import { LoginRequest, LoginResponse } from '../../features/manager/models/auth.model';

@Injectable({ providedIn: 'root' })
export class Auth {
  private readonly http = inject(HttpClient);
  private readonly authToken = inject(AuthTokenService);

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_BASE_URL}/auth/login`, credentials).pipe(
      tap((response) => {
        this.authToken.setToken(response.accessToken);
        this.authToken.setUser({
          userId: response.userId,
          email: response.email,
          fullName: response.fullName,
          role: response.role,
        });
      }),
    );
  }

  logout(): void {
    this.authToken.clear();
  }

  isAuthenticated(): boolean {
    return this.authToken.isAuthenticated();
  }
}
