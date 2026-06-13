import { Routes } from '@angular/router';
import { Dashboard } from './features/agent/pages/dashboard/dashboard';
import { NouveauTransfert } from './features/agent/pages/nouveau-transfert/nouveau-transfert';
import { ConfirmationTransfert } from './features/agent/pages/confirmation-transfert/confirmation-transfert';
import { ClientDashboardComponent } from './features/client/pages/dashboard/dashboard';
import { DetailTransfert } from './features/client/pages/detail-transfert/detail-transfert';
import { Historique } from './features/client/pages/historique/historique';
import { Profil } from './features/client/pages/profil/profil';
import { ClientLayoutComponent } from './features/client/pages/layout/layout';


export const routes: Routes = [
  {
    path: 'agent/dashboard',
    component: Dashboard,
  },
  {
    path: 'agent/nouveau-transfert',
    component: NouveauTransfert,
  },
  {
    path: 'agent/confirmation',
    component: ConfirmationTransfert,
  },
  {
    path: 'client',
    component: ClientLayoutComponent, // Ton layout avec la sidebar globale client
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: ClientDashboardComponent },
      { path: 'suivre-transfert', component: DetailTransfert },
      { path: 'historique', component: Historique },
      { path: 'profil', component: Profil },
    ]
  },
  {
    path: '',
    redirectTo: 'agent/dashboard',
    pathMatch: 'full',
  },
];
