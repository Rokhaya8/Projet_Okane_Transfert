import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../components/navbar/navbar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-nouveau-transfert',
  imports: [RouterLink, FormsModule, NavbarComponent],
  templateUrl: './nouveau-transfert.html',
  styleUrl: './nouveau-transfert.css',
})
export class NouveauTransfert {
  currentStep = 1;

  // Pays de l'agence (viendra du token JWT plus tard) à faire en dur pour l'instant
  paysAgence = 'Maroc';

  // Devise de l'agence (viendra du backend plus tard) à faire en dur pour l'instant
  deviseAgence = 'MAD';

  expediteur = {
    nom: '',
    prenom: '',
    typeIdentite: 'CIN',
    numIdentite: '',
    telephone: '',
    pays: '',
  };

  beneficiaire = {
    nom: '',
    prenom: '',
    telephone: '',
    pays: '',
  };

  montant = 0;
  modeReception = 'CASH';

  nextStep() {
    if (this.currentStep < 3) this.currentStep++;
  }

  prevStep() {
    if (this.currentStep > 1) this.currentStep--;
  }

  constructor(private router: Router) {}

  confirmerTransfert() {
    this.router.navigate(['/agent/confirmation']);
  }
}
