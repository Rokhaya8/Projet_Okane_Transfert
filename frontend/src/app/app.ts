import { Component, inject, OnInit,signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ApiService } from './services/api'; // Importe le service

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private apiService = inject(ApiService);

  protected agent = signal<any>(null);

  ngOnInit() {
    this.apiService.getAgentProfile(1).subscribe({ //Ici on a fixé le id à rendre dynamique avec authentification
      next: (data) => {
        // On envoie la donnée dans le signal 'agent'
        this.agent.set(data);
      },
      error: (err) => console.error('Erreur :', err)
    });
  }
}
