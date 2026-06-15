import { Component, EventEmitter, Input, OnInit, Output, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { ClientStateService } from '../../../core/services/client-state';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar implements OnInit {
  @Input() userRole: 'AGENT' | 'CLIENT' | 'ADMIN' = 'AGENT';
  @Input() isMobileMenuOpen: boolean = false;
  @Output() requestClose = new EventEmitter<void>();

  private clientState = inject(ClientStateService);
  private router = inject(Router);

  get clientDashboardLink(): string {
    const id = this.clientState.currentClient()?.id;
    return id ? `/client/${id}/dashboard` : '/client/1/dashboard';
  }

  get clientHistoriqueLink(): string {
    const id = this.clientState.currentClient()?.id;
    return id ? `/client/${id}/historique` : '/client/1/historique';
  }

  get clientSuivreLink(): string {
    const id = this.clientState.currentClient()?.id;
    return id ? `/client/${id}/suivre-transfert` : '/client/1/suivre-transfert';
  }

  ngOnInit(): void {}

  // Ferme le menu automatiquement quand on clique sur un lien mobile
  closeMenu(): void {
    this.requestClose.emit();
  }

  logout(): void {
    console.log('Déclenchement de la procédure de déconnexion...');

    // ÉTAPE A : Nettoyage local (Sécurité de la Session)
    // On supprime le jeton JWT et les infos utilisateur stockés dans le navigateur
    localStorage.removeItem('token'); 
    localStorage.removeItem('user_role');
    
    // sessionStorage.clear();

    // ÉTAPE B : Redirection programmée vers l'authentification
    this.router.navigate(['/auth/login']);
  }
}
