import { Routes } from '@angular/router';
import { Agences } from './features/admin/pages/agences/agences';
import { CurrencyManagementComponent } from './features/admin/pages/currency-management/currency-management';

export const routes: Routes = [

  {
    path: '',
    redirectTo: 'admin/agences',
    pathMatch: 'full'
  },

  {
    path: 'admin/agences',
    component: Agences
  },

  {
    path: 'admin/devises',
    component: CurrencyManagementComponent
  }

];