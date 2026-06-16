import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatBotApiService {
  // L'URL exacte de ton @RestController
  private baseUrl = 'http://localhost:8080/api/client/chat';
  private http = inject(HttpClient);

  // Une seule méthode pour envoyer le message
  sendMessage(userMessage: string, history: any[]): Observable<{ response: string }> {
   // 1. On extrait uniquement les 4 derniers messages (sliding window)
    // On ignore les messages techniques ou d'erreur s'il y en a eu
    const recentHistory = history.slice(-4).map(msg => ({
      // L'API Groq attend "user" ou "assistant" (pas "bot")
      role: msg.sender === 'user' ? 'user' : 'assistant',
      content: msg.text
    }));

    // 2. On envoie le message ET l'historique dans le JSON
    const body = { 
      message: userMessage,
      history: recentHistory 
    };
    return this.http.post<{ response: string }>(this.baseUrl, body, { withCredentials: true });
  }
}