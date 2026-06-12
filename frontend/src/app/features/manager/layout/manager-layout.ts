import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from '../../../shared/components/header/header';
import { NavItem, Sidebar } from '../../../shared/components/sidebar/sidebar';

@Component({
  selector: 'app-manager-layout',
  imports: [RouterOutlet, Sidebar, Header],
  templateUrl: './manager-layout.html',
  styleUrl: './manager-layout.css',
})
export class ManagerLayout {
  readonly sidebarOpen = signal(false);

  readonly navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/manager/dashboard' },
    { label: 'Agents', icon: 'groups', route: '/manager/agents' },
    { label: 'Opérations', icon: 'swap_horiz', route: '/manager/operations' },
    { label: 'Validations', icon: 'verified', route: '/manager/validations' },
    { label: 'Rapports', icon: 'assessment', route: '/manager/reports' },
    { label: 'Profil', icon: 'person', route: '/manager/profile' },
  ];

  toggleSidebar(): void {
    this.sidebarOpen.update((v) => !v);
  }
}
