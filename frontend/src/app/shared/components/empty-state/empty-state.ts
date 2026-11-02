import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-empty-state',
  imports: [MatIconModule],
  templateUrl: './empty-state.html',
  styleUrl: './empty-state.css',
})
export class EmptyState {
  @Input() icon = 'inbox';
  @Input() title = 'Aucune donnée';
  @Input() message = 'Aucun élément à afficher pour le moment.';
}
