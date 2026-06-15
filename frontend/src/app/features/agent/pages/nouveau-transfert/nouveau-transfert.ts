// --- 1. IMPORTATIONS : Le magasin d'outils ---
// On importe ici tout ce dont le composant a besoin pour fonctionner (services, composants tiers, outils Angular)
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Sidebar } from '../../../../shared/components/sidebar/sidebar';
import { Header } from '../../../../shared/components/header/header';
import { AgentService } from '../../../../services/agent';

// --- 2. CONFIGURATION DU COMPOSANT : La carte d'identité ---
@Component({
  standalone: true,
  selector: 'app-nouveau-transfert',
  imports: [RouterLink, FormsModule, Sidebar, Header],
  templateUrl: './nouveau-transfert.html',
  styleUrl: './nouveau-transfert.css',
})
export class NouveauTransfert implements OnInit {

  // --- 3. INJECTIONS : Les outils externes nécessaires ---
  private readonly agentService = inject(AgentService); // Pour communiquer avec l'API
  private readonly cdr = inject(ChangeDetectorRef);     // Pour forcer la mise à jour de l'affichage
  private readonly router = inject(Router);             // Pour changer de page après confirmation

  // --- 4. ÉTAT DU FORMULAIRE : La mémoire temporaire ---
  currentStep = 1;              // Garde en mémoire l'étape active du stepper
  telephoneSaisie = '';         // Stocke le numéro saisi par l'agent
  montant = 0;                  // Stocke le montant à transférer
  modeReception = 'CASH';       // Stocke le choix du mode de retrait

  // Objets pour regrouper les données saisies par l'utilisateur
  expediteur = { nom: '', prenom: '', typeIdentite: 'CIN', numIdentite: '', telephone: '', pays: '' };
  beneficiaire = { nom: '', prenom: '', telephone: '', pays: '' };

  // Constantes de configuration pour éviter de modifier ces valeurs par erreur
  readonly paysAgence = 'Maroc';
  deviseAgence = '';   // valeur par défaut le temps que l'API charge

  // Dictionnaire qui fait le lien entre un pays et son indicatif téléphonique
  private readonly indicateurs: { [key: string]: string } = {
    'Maroc': '+212', 'Algérie': '+213', 'Tunisie': '+216',
    'Sénégal': '+221', 'Côte d\'Ivoire': '+225', 'Mali': '+223',
    'Burkina Faso': '+226', 'Guinée': '+224', 'France': '+33',
    'Belgique': '+32', 'Espagne': '+34', 'Italie': '+39'
  };

  // La liste des pays de réception qui vient de l'API
  paysReception: any[] = [];

  // --- 5. INITIALISATION : Le point de démarrage ---
  ngOnInit(): void {
    this.chargerProfilAgent(); // On lance le chargement des données dès l'ouverture
  }

  // --- 6. LOGIQUE MÉTIER : Les outils de calcul ---
  // Ici id=1 : À MODIFIER quand l'agent se connectera (l'id viendra du token JWT)
  private chargerProfilAgent(): void {
    this.agentService.getAgentProfile(1).subscribe({
      next: (data: any) => {
        this.expediteur.pays = data.country;
        this.chargerPaysReception(data.country);  // enchaîne avec le country reçu
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erreur lors du chargement du profil:', err)
    });
  }

  // Charge les pays de réception disponibles depuis le pays de l'agent
  private chargerPaysReception(sourceCountry: string): void {
    this.agentService.getReceptionCountries(sourceCountry).subscribe({
      next: (data: any[]) => {
        this.paysReception = data;
        // La devise d'envoi est la même pour tous les corridors → prends le 1er
        if (data.length > 0) {
          this.deviseAgence = data[0].sourceCurrencyCode;
        }
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erreur chargement pays réception:', err)
    });
  }

  // Pour l'expéditeur (pays de l'agent) — utilise le dictionnaire
  getIndicatif(): string {
    return this.indicateurs[this.expediteur.pays] || '';
  }

  // Pour le bénéficiaire (pays de réception choisi) — utilise l'API
  getIndicatifBeneficiaire(): string {
    const pays = this.paysReception.find(p => p.country === this.beneficiaire.pays);
    return pays ? pays.phoneCode : '';
  }

  // --- 7. NAVIGATION & ACTIONS : La gestion des clics ---
  nextStep(): void {
    if (this.currentStep < 3) this.currentStep++;
  }

  prevStep(): void {
    if (this.currentStep > 1) this.currentStep--;
  }

  confirmerTransfert(): void {
    this.router.navigate(['/agent/confirmation']);
  }
}
