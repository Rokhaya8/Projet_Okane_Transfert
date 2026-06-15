import { Routes } from '@angular/router';
import { managerRoutes } from './features/manager/manager.routes';

export const routes: Routes = [
  { path: '', redirectTo: 'manager', pathMatch: 'full' },
  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/pages/login/login').then((m) => m.Login),
  },
  {
    path: 'manager',
    children: managerRoutes,
  },
  { path: '**', redirectTo: 'manager' },
];
