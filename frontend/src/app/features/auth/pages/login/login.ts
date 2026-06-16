import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})

export class Login {

  credentials = {
    email: '',
    password: '',
    role: 'CLIENT' // Rôle par défaut
  };

  login() {
    // Logique d'appel au service d'authentification
    console.log('Tentative de connexion:', this.credentials);
  }
}