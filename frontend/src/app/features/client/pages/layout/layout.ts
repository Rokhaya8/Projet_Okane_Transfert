import { Component, signal, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from '../../../../shared/components/sidebar/sidebar';
import { Header } from '../../../../shared/components/header/header';
import { ClientStateService } from '../../../../core/services/client-state';
@Component({
	selector: 'app-client-layout',
	imports: [RouterOutlet, Sidebar, Header],
	templateUrl: './layout.html',
	styleUrl: './layout.css',
})
export class ClientLayoutComponent {
	private clientState = inject(ClientStateService);
	readonly isMobileMenuOpen = signal(false);

	get userName(): string { return this.clientState.currentClient()?.fullName ?? 'Utilisateur'; }
	get userEmail(): string { return this.clientState.currentClient()?.email ?? ''; }

	toggleMobileMenu(): void {
		this.isMobileMenuOpen.update(isOpen => !isOpen);
	}

	closeMobileMenu(): void {
		this.isMobileMenuOpen.set(false);
	}
}
