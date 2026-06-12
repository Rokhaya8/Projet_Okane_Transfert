import { Component, Input } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-badge-statut',
  imports: [NgClass],
  templateUrl: './badge-statut.html',
  styleUrl: './badge-statut.css',
})
export class BadgeStatut {
  @Input({ required: true }) status = '';

  get statusClass(): string {
    switch (this.status?.toUpperCase()) {
      case 'PAID':
      case 'APPROVED':
      case 'ACTIVE':
        return 'badge-success';
      case 'PENDING':
        return 'badge-warning';
      case 'CANCELLED':
      case 'REJECTED':
      case 'EXPIRED':
        return 'badge-danger';
      default:
        return 'badge-neutral';
    }
  }

  get label(): string {
    const labels: Record<string, string> = {
      PENDING: 'En attente',
      PAID: 'Payé',
      CANCELLED: 'Annulé',
      EXPIRED: 'Expiré',
      APPROVED: 'Approuvé',
      REJECTED: 'Rejeté',
    };
    return labels[this.status?.toUpperCase()] ?? this.status;
  }
}
