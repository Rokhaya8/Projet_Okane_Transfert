import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-badge-statut',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span class="badge" [class]="'badge-' + status?.toLowerCase()">
      {{ status }}
    </span>`,
  styleUrl: './badge-statut.css'
})
export class BadgeStatut {
  @Input() status: string = '';
}
