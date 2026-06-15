import { Routes } from '@angular/router';
import { Agences } from './features/admin/pages/agences/agences';
import { CurrencyManagementComponent } from './features/admin/pages/currency-management/currency-management';
import { DashboardComponent } from './features/admin/pages/dashboard/dashboard';
import { AuditLogComponent } from './features/admin/pages/audit/audit';
import { AdminReportComponent } from './features/admin/pages/rapports/rapports'
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
  },
   { path: 'admin/dashboard', component: DashboardComponent },
    {
    path: 'admin/audit',
    component: AuditLogComponent
  },
  {
    path:'admin/report',
    component: AdminReportComponent
  }

];