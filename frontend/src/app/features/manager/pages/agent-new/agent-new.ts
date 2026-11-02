import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AgentService } from '../../services/agent.service';

@Component({
  selector: 'app-agent-new',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
  ],
  templateUrl: './agent-new.html',
  styleUrls: ['./agent-new.css', '../../styles/manager-shared.css'],
})
export class AgentNew {
  private readonly fb = inject(FormBuilder);
  private readonly agentService = inject(AgentService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  submitting = false;
  error = '';

  readonly form = this.fb.nonNullable.group({
    fullName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', Validators.required],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.error = '';
    this.agentService.createAgent(this.form.getRawValue()).subscribe({
      next: () => {
        this.snackBar.open('Agent créé avec succès', 'OK', { duration: 3000 });
        this.router.navigate(['/manager/agents']);
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Erreur lors de la création';
        this.submitting = false;
      },
    });
  }
}
