import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { Router } from '@angular/router';
import { Auth } from '../../../core/services/auth';
import { AuthTokenService } from '../../../core/services/auth-token.service';

@Component({
  selector: 'app-header',
  imports: [MatButtonModule, MatIconModule, MatMenuModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  @Input() pageTitle = '';
  @Output() menuToggle = new EventEmitter<void>();

  private readonly auth = inject(Auth);
  private readonly authToken = inject(AuthTokenService);
  private readonly router = inject(Router);

  get userName(): string {
    return this.authToken.getUser()?.fullName ?? 'Responsable';
  }

  get userEmail(): string {
    return this.authToken.getUser()?.email ?? '';
  }

  toggleMenu(): void {
    this.menuToggle.emit();
  }

  goProfile(): void {
    this.router.navigate(['/manager/profile']);
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/auth/login']);
  }
}
