import { Injectable, inject, signal } from '@angular/core'; // Importe signal pour stocker l'état
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root' // Le service est disponible partout dans l'application
})
export class ApiService {
  // L'URL de base pour contacter ton serveur Java
  private baseUrl = 'http://localhost:8081/okane_transfer_war_exploded/api';

  // Injection du client HTTP pour faire des requêtes vers le serveur
  private http = inject(HttpClient);

  // Création d'un signal pour garder en mémoire les données de l'agent
  // Cela permet à n'importe quel composant de lire la donnée stockée
  protected agentData = signal<any>(null);

  /**
   * Récupère le profil d'un agent depuis le Back-end
   * @param id L'identifiant de l'agent à chercher
   */
  getAgentProfile(id: number): Observable<any> {
    // Effectue une requête GET vers l'URL construite dynamiquement
    return this.http.get(`${this.baseUrl}/agent/profile/${id}`);
  }

  /**
   * Méthode pour mettre à jour le signal avec les données reçues
   * @param data Les données de l'agent
   */
  setAgentData(data: any): void {
    this.agentData.set(data);
  }
}
