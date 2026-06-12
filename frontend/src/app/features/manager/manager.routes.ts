import { Routes } from '@angular/router';
import { managerGuard } from '../../core/guards/manager-guard';

export const managerRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('./layout/manager-layout').then((m) => m.ManagerLayout),
    canActivate: [managerGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard').then((m) => m.Dashboard),
      },
      {
        path: 'agents',
        loadComponent: () => import('./pages/agents/agents').then((m) => m.Agents),
      },
      {
        path: 'agents/new',
        loadComponent: () => import('./pages/agent-new/agent-new').then((m) => m.AgentNew),
      },
      {
        path: 'agents/:id',
        loadComponent: () => import('./pages/agent-detail/agent-detail').then((m) => m.AgentDetailPage),
      },
      {
        path: 'operations',
        loadComponent: () => import('./pages/operations/operations').then((m) => m.Operations),
      },
      {
        path: 'operations/:id',
        loadComponent: () =>
          import('./pages/operation-detail/operation-detail').then((m) => m.OperationDetailPage),
      },
      {
        path: 'validations',
        loadComponent: () => import('./pages/validations/validations').then((m) => m.Validations),
      },
      {
        path: 'reports',
        loadComponent: () => import('./pages/reports/reports').then((m) => m.Reports),
      },
      {
        path: 'profile',
        loadComponent: () => import('./pages/profile/profile').then((m) => m.ProfilePage),
      },
    ],
  },
];
