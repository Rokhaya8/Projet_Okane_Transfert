import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { AgenceService, Agence, AgenceRequest } from '../../../../core/services/agence.service';

@Component({
  selector: 'app-agences',
  standalone: true,
  imports: [CommonModule, MatIconModule, FormsModule],
  templateUrl: './agences.html',
  styleUrl: './agences.css',
})
export class Agences implements OnInit {

  agences: Agence[] = [];
  agencesFiltrees: Agence[] = [];

  recherche = '';
  filtreStatut = '';
  filtreCountry = '';
  pays: string[] = [];

  afficherModal = false;
  modeEdition = false;
  agenceEnEdition: Agence | null = null;
  chargement = false;
  erreurMessage = '';

  formulaire: AgenceRequest = {
    name: '',
    address: '',
    country: '',
    dailyLimit: 0,
    active: true,
    managerId: undefined,
  };

  constructor(
    private agenceService: AgenceService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.chargerAgences();
  }

  chargerAgences(): void {
    console.log('chargerAgences called');
    this.agenceService.getAll().subscribe({
      next: (data) => {
        this.agences = data;
        this.agencesFiltrees = [...data];
        this.pays = [...new Set(data.map(a => a.country))];
        this.cdr.detectChanges();
        console.log('agences loaded', data.length);
      },
      error: (err) => {
        console.error('Erreur chargement agences', err);
      },
    });
  }

  appliquerFiltres(): void {
    this.agencesFiltrees = this.agences.filter(agence => {
      const matchRecherche =
        agence.name.toLowerCase().includes(this.recherche.toLowerCase()) ||
        agence.address.toLowerCase().includes(this.recherche.toLowerCase());
      const matchStatut =
        !this.filtreStatut ||
        (this.filtreStatut === 'Actif' ? agence.active : !agence.active);
      const matchPays = !this.filtreCountry || agence.country === this.filtreCountry;
      return matchRecherche && matchStatut && matchPays;
    });
  }

  onRecherche(event: Event): void {
    this.recherche = (event.target as HTMLInputElement).value;
    this.appliquerFiltres();
  }

  onFiltreStatut(event: Event): void {
    this.filtreStatut = (event.target as HTMLSelectElement).value;
    this.appliquerFiltres();
  }

  onFiltreCountry(event: Event): void {
    this.filtreCountry = (event.target as HTMLSelectElement).value;
    this.appliquerFiltres();
  }

  creerAgence(): void {
    this.modeEdition = false;
    this.agenceEnEdition = null;
    this.formulaire = { name: '', address: '', country: '', dailyLimit: 0, active: true };
    this.erreurMessage = '';
    this.afficherModal = true;
  }

  modifierAgence(agence: Agence): void {
    this.modeEdition = true;
    this.agenceEnEdition = agence;
    this.formulaire = {
      name: agence.name,
      address: agence.address,
      country: agence.country,
      dailyLimit: agence.dailyLimit,
      active: agence.active,
      managerId: agence.manager?.id,
    };
    this.erreurMessage = '';
    this.afficherModal = true;
  }

  fermerModal(): void {
    this.afficherModal = false;
    this.chargement = false;
  }

  soumettre(): void {
    if (!this.formulaire.name || !this.formulaire.address || !this.formulaire.country) {
      this.erreurMessage = 'Veuillez remplir tous les champs obligatoires.';
      return;
    }
    this.chargement = true;
    this.erreurMessage = '';

    const handleSuccess = () => {
      this.chargement = false;
      this.afficherModal = false;
      this.chargerAgences();
    };

    const handleError = (err: any, message: string) => {
      console.error(message, err);
      const backendMessage = err?.error?.message;
      if (err.status === 0 || err.status === 201 || err.status === 200) {
        handleSuccess();
      } else {
        this.erreurMessage = backendMessage || message;
        this.chargement = false;
      }
    };

    if (this.modeEdition && this.agenceEnEdition) {
      this.agenceService.modifier(this.agenceEnEdition.id, this.formulaire).subscribe({
        next: () => handleSuccess(),
        error: (err) => handleError(err, 'Erreur lors de la modification.'),
      });
    } else {
      this.agenceService.creer(this.formulaire).subscribe({
        next: () => handleSuccess(),
        error: (err) => handleError(err, 'Erreur lors de la création.'),
      });
    }
  }

  supprimerAgence(agence: Agence): void {
    if (!confirm(`Supprimer l'agence "${agence.name}" ?`)) return;

    console.log('Supprimer agence', agence.id);
    this.agenceService.supprimer(agence.id).subscribe({
      next: (response) => {
        console.log('Suppression réponse', response.status);
        if (response.status === 204 || response.status === 200) {
          this.agences = this.agences.filter(a => a.id !== agence.id);
          this.appliquerFiltres();
          this.cdr.detectChanges();
        } else {
          console.error('Suppression inattendue', response);
          this.erreurMessage = 'Échec suppression : statut inattendu ' + response.status;
        }
      },
      error: (err) => {
        console.error('Erreur suppression', err);
        this.erreurMessage = err?.error?.message || 'Erreur lors de la suppression.';
      },
    });
  }
}