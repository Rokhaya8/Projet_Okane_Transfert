import { Component } from '@angular/core';

@Component({
  selector: 'app-topbar',
  imports: [],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class TopbarComponent {
  userName = 'Admin Principal';
  userEmail = 'admin@okanetransfer.com';
  userInitials = 'AP';

  logout(): void {
    // logique de déconnexion
  }
}
