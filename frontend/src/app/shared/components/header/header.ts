import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  imports: [CommonModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements OnInit {
  // Propriétés dynamiques reçues depuis le HTML parent
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
    return this.userName
      .split(' ')
      .map(name => name.charAt(0))
      .join('')
      .toUpperCase()
      .substring(0, 2);
  }

  toggleMobileMenu(): void {
    this.menuToggle.emit();
  }

  onLogout(): void {
    console.log('Déconnexion cliquée !');
  }
}
