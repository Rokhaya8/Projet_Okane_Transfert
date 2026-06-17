import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api';

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/auth/login`, { email, password })
      .pipe(
        tap(response => {
          const token = response.accessToken ?? response.token;
          localStorage.setItem('token', token);
          localStorage.setItem('fullName', response.fullName);
          localStorage.setItem('role', response.role);
          localStorage.setItem('userId', (response.userId ?? response.id ?? '').toString());
        })
      );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('fullName');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');  // ← AJOUT
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }
}
