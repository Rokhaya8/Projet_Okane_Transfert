import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const managerGuard: CanActivateFn = () => {
  const router = inject(Router);
  const token = localStorage.getItem('token') ?? localStorage.getItem('okane_access_token');
  const role = localStorage.getItem('role');

  if (token && role === 'ROLE_MANAGER') {
    return true;
  }

  return router.createUrlTree(['/auth/login']);
};
