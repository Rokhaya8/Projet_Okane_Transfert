import { Routes } from '@angular/router';

// Agent
import { Dashboard } from './features/agent/pages/dashboard/dashboard';
import { NouveauTransfert } from './features/agent/pages/nouveau-transfert/nouveau-transfert';
import { ConfirmationTransfert } from './features/agent/pages/confirmation-transfert/confirmation-transfert';
import { PayerTransfert } from './features/agent/pages/payer-transfert/payer-transfert';
import { Caisse } from './features/agent/pages/caisse/caisse';
import { AgentHistorique } from './features/agent/pages/historique/historique';

// Client
import { ClientDashboardComponent } from './features/client/pages/dashboard/dashboard';
import { DetailTransfert } from './features/client/pages/detail-transfert/detail-transfert';
import { Historique } from './features/client/pages/historique/historique';
import { Profil } from './features/client/pages/profil/profil';
import { ClientLayoutComponent } from './features/client/pages/layout/layout';

// Auth
import { Login } from './features/auth/pages/login/login';
import { InscriptionComponent } from './features/auth/pages/inscription/inscription';

// Admin
import { Agences } from './features/admin/pages/agences/agences';
import { CurrencyManagementComponent } from './features/admin/pages/currency-management/currency-management';
import { DashboardComponent } from './features/admin/pages/dashboard/dashboard';
import { AuditLogComponent } from './features/admin/pages/audit/audit';
import { AdminReportComponent } from './features/admin/pages/rapports/rapports';

// Manager
import { managerRoutes } from './features/manager/manager.routes';

export const routes: Routes = [
  // Auth
  { path: 'auth/login', component: Login },
  { path: 'auth/inscription', component: InscriptionComponent },
  { path: 'auth', redirectTo: 'auth/login', pathMatch: 'full' },

  // Agent
  { path: 'agent/dashboard', component: Dashboard },
  { path: 'agent/nouveau-transfert', component: NouveauTransfert },
  { path: 'agent/confirmation', component: ConfirmationTransfert },
  { path: 'agent/payer-transfert', component: PayerTransfert },
  { path: 'agent/caisse', component: Caisse },
  { path: 'agent/historique', component: AgentHistorique },

  // Client
  {
    path: 'client',
    component: ClientLayoutComponent,
    children: [
      {
        path: ':clientId',
        children: [
          { path: 'dashboard', component: ClientDashboardComponent },
          { path: 'historique', component: Historique },
          { path: 'suivre-transfert', component: DetailTransfert },
          { path: 'profil', component: Profil },
        ]
      }
    ]
  },

  // Admin
  { path: 'admin/agences', component: Agences },
  { path: 'admin/devises', component: CurrencyManagementComponent },
  { path: 'admin/dashboard', component: DashboardComponent },
  { path: 'admin/audit', component: AuditLogComponent },
  { path: 'admin/report', component: AdminReportComponent },

  // Manager
  {
    path: 'manager',
    children: managerRoutes
  },

  // Défaut → login
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
];
