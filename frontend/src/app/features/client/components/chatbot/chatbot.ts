import { Component, inject, signal, ViewChild, ElementRef, AfterViewChecked, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatBotApiService } from '../../../../core/services/chatBot';
import { Subscription } from 'rxjs';

interface ChatMessage {
  sender: 'user' | 'bot';
  text: string;
  timestamp: Date;
}

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: 'chatbot.html',
  styleUrls: ['chatbot.css']
})
export class ChatBot implements OnInit, AfterViewChecked, OnDestroy {
  private chatService = inject(ChatBotApiService);
  
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  userInput = signal('');
  messages = signal<ChatMessage[]>([
    { 
      sender: 'bot', 
      text: 'Bonjour ! Je suis l\'assistant officiel d\'Okane Transfert. Je peux vous renseigner sur nos frais, le statut de vos envois ou nos délais.', 
      timestamp: new Date() 
    }
  ]);
  isTyping = signal(false);
  isOpen = signal(false);
  
  private subscriptions: Subscription = new Subscription();

  ngOnInit() {
    // Plus besoin d'initialiser de session
  }

  // Permet de scroller automatiquement en bas quand un message s'ajoute
  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  // Ouvre ou ferme la fenêtre du chat
  toggleChat(): void {
    this.isOpen.update(v => !v);
  }

  // Méthode d'envoi simplifiée
  send(): void {
    const text = this.userInput().trim();
    if (!text) return;

    // capture l'historique actuel (avant d'ajouter le nouveau message)
      const currentHistory = this.messages();
    // Afficher immédiatement le message de l'utilisateur dans l'UI
    this.messages.update(prev => [...prev, { sender: 'user', text, timestamp: new Date() }]);
    this.userInput.set('');
    this.isTyping.set(true);

    // Appel direct à ton backend simplifié
    this.subscriptions.add(
      this.chatService.sendMessage(text, currentHistory).subscribe({
        next: (res) => {
          this.messages.update(prev => [...prev, { 
            sender: 'bot', 
            text: res.response, // Reçoit 'response' de Spring Boot
            timestamp: new Date()
          }]);
          this.isTyping.set(false);
        },
        error: (err) => {
          console.error('Erreur API Chatbot:', err);
          this.addBotMessage('Une erreur de communication est survenue avec le service.');
          this.isTyping.set(false);
        }
      })
    );
  }

  // Ajoute un message générique venant du bot (ex: erreur réseau)
  private addBotMessage(text: string): void {
    this.messages.update(prev => [...prev, { 
      sender: 'bot', 
      text, 
      timestamp: new Date() 
    }]);
  }

  // Formate les sauts de ligne et le gras envoyés par l'IA
  formatMessage(text: string): string {
    if (!text) return '';
    let formatted = text.replace(/\n/g, '<br>');
    formatted = formatted.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
    return formatted;
  }

  // Gère le scroll vers le bas
  private scrollToBottom(): void {
    try {
      if (this.scrollContainer) {
        this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
      }
    } catch (err) {}
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }
}