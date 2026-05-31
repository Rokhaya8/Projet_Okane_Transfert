import { Routes } from '@angular/router';
import { Dashboard } from './features/agent/pages/dashboard/dashboard';
import { NouveauTransfert } from './features/agent/pages/nouveau-transfert/nouveau-transfert';
import { ConfirmationTransfert } from './features/agent/pages/confirmation-transfert/confirmation-transfert';

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
    path: '',
    redirectTo: 'agent/dashboard',
    pathMatch: 'full',
  },
];
