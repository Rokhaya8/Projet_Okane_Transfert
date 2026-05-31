import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-confirmation-transfert',
  imports: [RouterLink, NavbarComponent, DatePipe],
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
