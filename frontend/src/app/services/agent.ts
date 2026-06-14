import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root' // Indispensable pour que le service soit utilisable partout
})
export class AgentService {
  // L'URL de base qui pointe vers ton backend Java
  private baseUrl = 'http://localhost:8081/okane_transfer_war_exploded/api'; // Appelle le backend

  // Injection du client pour faire les requêtes HTTP
  private http = inject(HttpClient);

  // Signal pour stocker et partager l'état de l'agent dans ton appli
  protected agentData = signal<any>(null);

  /**
   * Récupère le profil de l'agent
   * @param id L'identifiant unique de l'agent
   */
  getAgentProfile(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/agent/profile/${id}`);
  }

  getReceptionCountries(sourceCountry: string) {
    return this.http.get<any[]>(
      'http://localhost:8081/okane_transfer_war_exploded/api/agent/reception-countries',
      { params: { sourceCountry } }
    );
  }

  /**
   * Met à jour le signal avec les données reçues
   */
  setAgentData(data: any): void {
    this.agentData.set(data);
  }
}
