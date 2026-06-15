import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../app/shared/components/sidebar/sidebar'
import { TopbarComponent } from '../app/shared/components/header/header';
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SidebarComponent, TopbarComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
}
