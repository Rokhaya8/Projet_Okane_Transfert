import { CanActivateFn } from '@angular/router';

export const agentGuard: CanActivateFn = (route, state) => {
  return true;
};
