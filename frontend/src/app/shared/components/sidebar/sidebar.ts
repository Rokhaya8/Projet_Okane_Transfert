import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar implements OnInit {
  @Input() userRole: 'AGENT' | 'CLIENT' | 'ADMIN' = 'CLIENT';
  @Input() isMobileMenuOpen: boolean = false;
  @Output() requestClose = new EventEmitter<void>();

  constructor(){}

  ngOnInit(): void{}

  // Ferme le menu automatiquement quand on clique sur un lien mobile
  closeMenu(): void {
    this.requestClose.emit();
  }

  logout() {
    console.log('Déconnexion');
  }
}
