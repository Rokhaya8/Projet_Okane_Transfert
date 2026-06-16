import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

// Header pour l'espace agent/client
@Component({
  selector: 'app-header',
  imports: [CommonModule],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header implements OnInit {
  @Input() pageTitle: string = 'Tableau de bord';
  @Input() userName: string = 'Utilisateur';
  @Input() userEmail: string = 'user@okanetransfer.com';
  @Input() isMobileMenuOpen: boolean = false;
  @Output() menuToggle = new EventEmitter<void>();

  ngOnInit(): void {
    this.pageTitle = this.pageTitle?.trim() || 'Tableau de bord';
    this.userName = this.userName?.trim() || 'Utilisateur';
    this.userEmail = this.userEmail?.trim() || 'user@okanetransfer.com';
  }

  getUserInitials(): string {
    if (!this.userName) return 'U';
    return this.userName.split(' ').map(n => n.charAt(0)).join('').toUpperCase().substring(0, 2);
  }

  toggleMobileMenu(): void { this.menuToggle.emit(); }
  onLogout(): void { console.log('Déconnexion cliquée !'); }
}

// Topbar pour l'espace admin
@Component({
  selector: 'app-topbar',
  imports: [],
  template: `
    <header class="topbar">
      <div class="topbar-right">
        <div class="user-info">
          <span class="user-name">{{ userName }}</span>
          <span class="user-email">{{ userEmail }}</span>
        </div>
        <div class="avatar">{{ userInitials }}</div>
        <button class="logout-btn" (click)="logout()" title="Déconnexion">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
            <polyline points="16 17 21 12 16 7"></polyline>
            <line x1="21" y1="12" x2="9" y2="12"></line>
          </svg>
        </button>
      </div>
    </header>`,
  styleUrl: './header.css'
})
export class TopbarComponent {
  userName = 'Admin Principal';
  userEmail = 'admin@okanetransfer.com';
  userInitials = 'AP';
  logout(): void {}
}
