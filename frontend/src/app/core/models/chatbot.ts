// Représente la session globale du chat
export interface ChatSession {
  id: number;
  userId: number;
  status: 'ACTIVE' | 'CLOSED';
  createdAt: string; 
  updatedAt?: string;
}

// Représente le message envoyé par l'utilisateur (le corps de la requête POST)
export interface ChatMessageRequest {
  content: string;
}

// Représente la réponse immédiate du bot après un envoi
export interface ChatMessageResponse {
  content: string;
  timestamp: string;
}

// Représente un message dans l'historique (qu'il vienne de l'utilisateur ou du bot)
export interface ChatMessageDto {
  content: string;
  sender: 'USER' | 'BOT';
  timestamp: string;
}