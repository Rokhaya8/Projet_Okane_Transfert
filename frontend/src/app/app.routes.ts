import { Routes } from '@angular/router';
import { Dashboard } from './features/agent/pages/dashboard/dashboard';
import { NouveauTransfert } from './features/agent/pages/nouveau-transfert/nouveau-transfert';
import { ConfirmationTransfert } from './features/agent/pages/confirmation-transfert/confirmation-transfert';
import { PayerTransfert } from './features/agent/pages/payer-transfert/payer-transfert';
import { Caisse } from './features/agent/pages/caisse/caisse';
import { Historique } from './features/agent/pages/historique/historique';

export const routes: Routes = [
  { path: 'agent/dashboard', component: Dashboard },
  { path: 'agent/nouveau-transfert', component: NouveauTransfert },
  { path: 'agent/confirmation', component: ConfirmationTransfert },
  { path: 'agent/payer-transfert', component: PayerTransfert },
  { path: 'agent/caisse', component: Caisse },
  { path: 'agent/historique', component: Historique },
  { path: '', redirectTo: 'agent/dashboard', pathMatch: 'full' },
];
