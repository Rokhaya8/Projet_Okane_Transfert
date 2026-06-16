import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AgentService } from '../../services/agent.service';
import { AgentDetail } from '../../models/agent.model';
import { BadgeStatut } from '../../../../shared/components/badge-statut/badge-statut';
import { formatAmount, formatDate } from '../../../../shared/utils/format.utils';

@Component({
  selector: 'app-agent-detail',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    BadgeStatut,
  ],
  templateUrl: './agent-detail.html',
  styleUrls: ['./agent-detail.css', '../../styles/manager-shared.css'],
})
export class AgentDetailPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly agentService = inject(AgentService);
  private readonly snackBar = inject(MatSnackBar);

  agent: AgentDetail | null = null;
  loading = true;
  editing = false;
  submitting = false;
  error = '';

  readonly formatDate = formatDate;
  readonly formatAmount = formatAmount;

  readonly form = this.fb.nonNullable.group({
    fullName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', Validators.required],
  });

  ngOnInit(): void {
    this.editing = this.route.snapshot.queryParamMap.get('edit') === 'true';
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadAgent(id);
  }

  loadAgent(id: number): void {
    this.loading = true;
    this.agentService.getAgent(id).subscribe({
      next: (agent) => {
        this.agent = agent;
        this.form.patchValue({
          fullName: agent.fullName,
          email: agent.email,
          phone: agent.phone,
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Agent introuvable';
        this.loading = false;
      },
    });
  }

  toggleEdit(): void {
    this.editing = !this.editing;
  }

  save(): void {
    if (!this.agent || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting = true;
    this.agentService.updateAgent(this.agent.id, this.form.getRawValue()).subscribe({
      next: (updated) => {
        this.agent = { ...this.agent!, ...updated };
        this.editing = false;
        this.submitting = false;
        this.snackBar.open('Agent mis à jour', 'OK', { duration: 3000 });
      },
      error: (err) => {
        this.snackBar.open(err?.error?.message ?? 'Erreur', 'Fermer', { duration: 4000 });
        this.submitting = false;
      },
    });
  }
}
