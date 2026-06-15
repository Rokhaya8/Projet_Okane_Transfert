import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  // Récupérer le token stocké
  const token = localStorage.getItem('token');

  // S'il y a un token, l'ajouter à l'en-tête Authorization
  if (token) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }

  // Sinon, laisser passer la requête telle quelle
  return next(req);
};
