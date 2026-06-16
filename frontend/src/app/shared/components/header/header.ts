import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
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

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [],
  template: `
    <header class="topbar">
      <div class="topbar-right">
        <div class="user-info">
          <span class="user-name">{{ userName }}</span>
          <span class="user-email">{{ userEmail }}</span>
        </div>
        <div class="avatar">{{ userInitials }}</div>
        <button class="logout-btn" (click)="logout()">↪</button>
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
