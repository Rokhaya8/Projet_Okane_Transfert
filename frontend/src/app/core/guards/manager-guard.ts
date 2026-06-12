import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthTokenService } from '../services/auth-token.service';

export const managerGuard: CanActivateFn = () => {
  const authToken = inject(AuthTokenService);
  const router = inject(Router);

  if (!authToken.isAuthenticated()) {
    return router.createUrlTree(['/auth/login']);
  }

  if (authToken.hasRole('ROLE_MANAGER')) {
    return true;
  }

  return router.createUrlTree(['/auth/login']);
};
