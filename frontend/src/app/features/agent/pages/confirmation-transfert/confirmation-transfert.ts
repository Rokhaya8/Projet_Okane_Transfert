import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Sidebar } from '../../../../shared/components/sidebar/sidebar'; // <-- VERIFIEZ CE CHEMIN
import { DatePipe } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-confirmation-transfert',
  imports: [RouterLink, Sidebar, DatePipe], // On retire le Header d'ici
  templateUrl: './confirmation-transfert.html',
  styleUrl: './confirmation-transfert.css',
})
export class ConfirmationTransfert {
  codeRetrait = 'OK-2F9A4B';
  montant = 100;
  frais = 60;
  montantRecu = 238;
  dateTransfert = new Date();
}
