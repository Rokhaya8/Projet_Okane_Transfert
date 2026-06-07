import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from '../../../../shared/components/sidebar/sidebar';
import { Header } from '../../../../shared/components/header/header';
@Component({
	selector: 'app-client-layout',
	imports: [RouterOutlet, Sidebar, Header],
	templateUrl: './layout.html',
	styleUrl: './layout.css',
})
export class ClientLayoutComponent {
	readonly isMobileMenuOpen = signal(false);

	toggleMobileMenu(): void {
		this.isMobileMenuOpen.update(isOpen => !isOpen);
	}

	closeMobileMenu(): void {
		this.isMobileMenuOpen.set(false);
	}
}
