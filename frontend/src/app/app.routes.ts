import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'manager/dashboard', pathMatch: 'full' },
  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/pages/login/login').then((m) => m.Login),
  },
  {
    path: 'manager',
    loadChildren: () => import('./features/manager/manager.routes').then((m) => m.managerRoutes),
  },
  { path: '**', redirectTo: 'manager/dashboard' },
];
