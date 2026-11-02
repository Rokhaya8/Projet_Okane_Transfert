import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../../../core/services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private auth = inject(Auth);
  private router = inject(Router);
  email = '';
  password = '';
  errorMessage = '';
  loading = false;

  onSubmit(): void {
    this.errorMessage = '';
    this.loading = true;
    this.auth.login(this.email, this.password).subscribe({
      next: (response) => {
        this.loading = false;
        this.redirectByRole(response.role);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Email ou mot de passe incorrect';
        console.error('Erreur de connexion:', err);
      }
    });
  }

  private redirectByRole(role: string): void {
    switch (role) {
      case 'ROLE_AGENT':
        this.router.navigate(['/agent/dashboard']);
        break;
      case 'ROLE_ADMIN':
        this.router.navigate(['/admin/dashboard']);
        break;
      case 'ROLE_MANAGER':
        this.router.navigate(['/manager/dashboard']);
        break;
      case 'ROLE_CLIENT':
        const clientId = localStorage.getItem('userId');
        this.router.navigate(['/client', clientId, 'dashboard']);
        break;
      default:
        this.router.navigate(['/']);
    }
  }
}
