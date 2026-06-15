import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditLogService } from '../../../../core/services/audit-log.service';
import { AuditLog } from '../../../../core/models/audit-log.model';

@Component({
  selector: 'app-audit-log',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './audit.html',
  styleUrls: ['./audit.css']
})
export class AuditLogComponent implements OnInit {
  logs: AuditLog[] = [];
  filtered: AuditLog[] = [];
  loading = false;
  error = '';

  searchTerm = '';
  selectedUser = '';
  selectedAction = '';

  users: string[] = [];
  actions: string[] = [];

  constructor(private auditLogService: AuditLogService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loading = true;
    this.auditLogService.getAll().subscribe({
      next: (data) => {
        this.logs = data;
        this.filtered = data;
        this.users = [...new Set(data.map(l => l.username ?? '').filter(Boolean))];
        this.actions = [...new Set(data.map(l => l.action))];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Impossible de contacter le serveur.';
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    const term = this.searchTerm.toLowerCase();
    this.filtered = this.logs.filter(log => {
      const matchSearch =
        !term ||
        log.action.toLowerCase().includes(term) ||
        (log.username ?? '').toLowerCase().includes(term) ||
        log.ipAddress.includes(term) ||
        (log.details ?? '').toLowerCase().includes(term);

      const matchUser =
        !this.selectedUser || (log.username ?? '') === this.selectedUser;

      const matchAction =
        !this.selectedAction || log.action === this.selectedAction;

      return matchSearch && matchUser && matchAction;
    });
  }

  resetFilters(): void {
    this.searchTerm = '';
    this.selectedUser = '';
    this.selectedAction = '';
    this.filtered = [...this.logs];
  }

  getActionClass(action: string): string {
    const map: Record<string, string> = {
      CREATE: 'badge-create',
      UPDATE: 'badge-update',
      DELETE: 'badge-delete',
      LOGIN:  'badge-login'
    };
    return map[action?.toUpperCase()] ?? 'badge-default';
  }
}