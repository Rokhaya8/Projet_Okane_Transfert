import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProfileService } from '../../services/profile.service';
import { ManagerProfile } from '../../models/profile.model';

@Component({
  selector: 'app-manager-profile',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
  ],
  templateUrl: './profile.html',
  styleUrls: ['./profile.css', '../../styles/manager-shared.css'],
})
export class ProfilePage implements OnInit {
  private readonly profileService = inject(ProfileService);
  private readonly fb = inject(FormBuilder);
  private readonly snackBar = inject(MatSnackBar);

  profile: ManagerProfile | null = null;
  loading = true;
  editingProfile = false;
  editingPassword = false;
  submitting = false;
  error = '';
  success = '';

  readonly profileForm = this.fb.nonNullable.group({
    fullName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', Validators.required],
  });

  readonly passwordForm = this.fb.nonNullable.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmNewPassword: ['', Validators.required],
  });

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading = true;
    this.profileService.getProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.profileForm.patchValue({
          fullName: profile.fullName,
          email: profile.email,
          phone: profile.phone,
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Erreur lors du chargement';
        this.loading = false;
      },
    });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }
    this.submitting = true;
    this.profileService.updateProfile(this.profileForm.getRawValue()).subscribe({
      next: (updated) => {
        this.profile = updated;
        this.editingProfile = false;
        this.submitting = false;
        this.success = 'Profil mis à jour';
        this.snackBar.open('Profil mis à jour', 'OK', { duration: 3000 });
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Erreur';
        this.submitting = false;
      },
    });
  }

  savePassword(): void {
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }
    const { currentPassword, newPassword, confirmNewPassword } = this.passwordForm.getRawValue();
    if (newPassword !== confirmNewPassword) {
      this.error = 'Les mots de passe ne correspondent pas';
      return;
    }
    this.submitting = true;
    this.profileService.changePassword({ currentPassword, newPassword }).subscribe({
      next: () => {
        this.editingPassword = false;
        this.submitting = false;
        this.passwordForm.reset();
        this.snackBar.open('Mot de passe modifié', 'OK', { duration: 3000 });
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Erreur';
        this.submitting = false;
      },
    });
  }
}
