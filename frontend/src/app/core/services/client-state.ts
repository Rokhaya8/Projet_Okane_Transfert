import { Injectable, signal, effect } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';
import { ClientProfile } from '../models/client-dashboard.model';

@Injectable({ providedIn: 'root' })
export class ClientStateService {
  // Au démarrage, on regarde si un client était déjà stocké dans la session
  readonly currentClient = signal<ClientProfile | null>(this.getSavedClient());

  constructor() {
    // Chaque fois que currentClient change, on le sauvegarde automatiquement
    effect(() => {
      const client = this.currentClient();
      if (client) {
        sessionStorage.setItem('current_client', JSON.stringify(client));
      } else {
        sessionStorage.removeItem('current_client');
      }
    });
  }

  private getSavedClient(): ClientProfile | null {
    const saved = sessionStorage.getItem('current_client');
    try {
      return saved ? JSON.parse(saved) : null;
    } catch {
      return null;
    }
  }

  resolveClientId(routeSnapshot: ActivatedRouteSnapshot): number | null {
    if (this.currentClient()?.id) return this.currentClient()!.id;
    let r: ActivatedRouteSnapshot | null = routeSnapshot;
    while (r) {
      const id = r.paramMap.get('clientId');
      if (id) return +id;
      r = r.parent ?? null;
    }
    return null;
  }
}