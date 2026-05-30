markdown# OkaneTransfer — Plateforme de Gestion des Transferts d'Argent

Application web de transfert d'argent national et international, similaire à Western Union/MoneyGram.

---

## Structure du Projet
OkaneTransfer/
├── backend/      # Spring MVC 6 + Spring Security 6
├── frontend/     # Angular 17+
└── docker-compose.yml

---

## Backend — Spring MVC 6
backend/src/main/java/com/okanetransfer/
│
├── config/          # Configuration globale de l'application
│   ├── SecurityConfig.java      → Configuration Spring Security + JWT
│   ├── JwtConfig.java           → Paramètres des tokens JWT
│   ├── SwaggerConfig.java       → Documentation API avec Swagger UI
│   ├── CorsConfig.java          → Configuration CORS
│   └── WebMvcConfig.java        → Configuration Spring MVC
│
├── controller/      # Couche Présentation — endpoints REST
│   ├── AuthController.java      → Login, logout, refresh token
│   ├── AdminController.java     → Endpoints espace administrateur
│   ├── AgenceController.java    → CRUD agences
│   ├── AgentController.java     → Gestion des agents
│   ├── TransfertController.java → Envoi et paiement des transferts
│   ├── DeviseController.java    → Gestion des devises et taux
│   ├── TarifController.java     → Grilles tarifaires
│   ├── RapportController.java   → Rapports et statistiques
│   ├── KycController.java       → Conformité KYC/AML
│   └── ClientController.java    → Espace client self-service
│
├── dto/             # Objets de transfert de données (entrée/sortie API)
│   ├── request/     → DTOs reçus depuis le frontend
│   │   ├── LoginRequest.java
│   │   ├── TransfertRequest.java
│   │   ├── AgenceRequest.java
│   │   ├── DeviseRequest.java
│   │   └── UserRequest.java
│   └── response/    → DTOs envoyés vers le frontend
│       ├── LoginResponse.java
│       ├── TransfertResponse.java
│       ├── AgenceResponse.java
│       └── DashboardResponse.java
│
├── service/         # Couche Service — logique métier
│   ├── AuthService.java         → Authentification et gestion des tokens
│   ├── TransfertService.java    → Création, paiement, annulation des transferts
│   ├── AgenceService.java       → Gestion des agences
│   ├── AgentService.java        → Gestion des agents
│   ├── DeviseService.java       → Devises et taux de change
│   ├── TarifService.java        → Calcul des frais et grilles tarifaires
│   ├── CaisseService.java       → Gestion de caisse des agents
│   ├── RapportService.java      → Génération des rapports
│   ├── KycService.java          → Vérification KYC/AML
│   ├── NotificationService.java → Envoi SMS, Email, WhatsApp
│   └── AuditService.java        → Journal d'audit des actions
│
├── repository/      # Couche Persistance — accès base de données
│   ├── UserRepository.java
│   ├── TransfertRepository.java
│   ├── AgenceRepository.java
│   ├── AgentRepository.java
│   ├── DeviseRepository.java
│   ├── CorridorRepository.java
│   ├── TarifRepository.java
│   ├── CaisseRepository.java
│   ├── JournalAuditRepository.java
│   └── KycRepository.java
│
├── entity/          # Entités JPA — tables de la base de données
│   ├── User.java            → Utilisateur (admin, manager, agent, client)
│   ├── Transfert.java       → Transaction de transfert d'argent
│   ├── Agence.java          → Agence de transfert
│   ├── Agent.java           → Agent travaillant dans une agence
│   ├── Client.java          → Client expéditeur ou bénéficiaire
│   ├── Devise.java          → Devise (USD, EUR, MAD...)
│   ├── Corridor.java        → Route de transfert (ex: Maroc → Sénégal)
│   ├── Tarif.java           → Grille tarifaire par tranche
│   ├── Caisse.java          → Caisse d'une agence
│   ├── JournalAudit.java    → Log de toutes les actions sensibles
│   └── KycDossier.java      → Dossier de vérification d'identité
│
├── security/        # Sécurité JWT
│   ├── JwtTokenProvider.java        → Génération et validation des tokens
│   ├── JwtAuthFilter.java           → Filtre d'authentification par token
│   └── CustomUserDetailsService.java → Chargement des utilisateurs
│
└── exception/       # Gestion globale des erreurs
├── GlobalExceptionHandler.java      → Intercepte toutes les erreurs
├── TransfertNotFoundException.java  → Transfert introuvable
└── InsufficientFundsException.java  → Solde insuffisant

---

## Frontend — Angular 17+
frontend/src/app/
│
├── core/                    # Services globaux à toute l'application
│   ├── guards/              → Protection des routes par rôle
│   │   ├── auth.guard.ts        → Vérifie si l'utilisateur est connecté
│   │   ├── admin.guard.ts       → Accès réservé à l'administrateur
│   │   ├── agent.guard.ts       → Accès réservé aux agents
│   │   └── manager.guard.ts     → Accès réservé aux responsables
│   ├── interceptors/        → Traitement automatique des requêtes HTTP
│   │   └── jwt.interceptor.ts   → Ajoute le token JWT dans chaque requête
│   └── services/            → Appels API vers le backend
│       ├── auth.service.ts      → Login, logout, gestion des tokens
│       ├── transfert.service.ts → Créer, payer, suivre les transferts
│       └── agence.service.ts    → Gestion des agences
│
├── shared/                  # Éléments réutilisables dans toute l'app
│   ├── components/
│   │   ├── sidebar/             → Menu latéral (admin, agent, manager)
│   │   ├── header/              → Barre du haut avec avatar et notifications
│   │   └── badge-statut/        → Badge coloré EN_ATTENTE/PAYÉ/ANNULÉ
│   └── models/              → Interfaces TypeScript des données
│       ├── transfert.model.ts   → Structure d'un transfert
│       ├── agence.model.ts      → Structure d'une agence
│       └── user.model.ts        → Structure d'un utilisateur
│
└── features/                # Fonctionnalités séparées par rôle
│
├── auth/
│   ├── pages/
│   │   ├── login/           → Page de connexion
│   │   └── inscription/     → Page d'inscription client
│   └── components/
│       └── otp-input/       → Saisie du code OTP (2FA)
│
├── admin/
│   ├── pages/
│   │   ├── dashboard/       → Vue globale KPIs, alertes, transactions
│   │   ├── agences/         → Liste et gestion des agences
│   │   ├── devises/         → Devises, taux de change, corridors
│   │   ├── tarifs/          → Grilles tarifaires et simulateur
│   │   ├── utilisateurs/    → Gestion des comptes utilisateurs
│   │   ├── rapports/        → Rapports financiers et statistiques
│   │   ├── audit/           → Journal d'audit des actions
│   │   └── kyc/             → Conformité et vérification d'identité
│   └── components/
│       ├── agence-card/         → Carte affichant les infos d'une agence
│       ├── agence-modal/        → Popup création/édition d'agence
│       ├── tarif-simulateur/    → Simulateur de calcul des frais
│       └── corridor-toggle/     → Bouton ON/OFF d'un corridor
│
├── agent/
│   ├── pages/
│   │   ├── nouveau-transfert/   → Formulaire envoi (stepper 3 étapes)
│   │   ├── payer-transfert/     → Paiement par code de retrait
│   │   └── caisse/              → Gestion et clôture de caisse
│   └── components/
│       ├── stepper/             → Composant stepper réutilisable
│       ├── expediteur-form/     → Formulaire informations expéditeur
│       ├── beneficiaire-form/   → Formulaire informations bénéficiaire
│       └── code-retrait-card/   → Carte affichant le code généré
│
├── manager/
│   ├── pages/
│   │   ├── dashboard/       → KPIs de l'agence et activité des agents
│   │   ├── agents/          → Liste et gestion des agents de l'agence
│   │   └── validations/     → File d'attente des opérations à valider
│   └── components/
│       └── validation-card/ → Carte d'une opération en attente
│
└── client/
├── pages/
│   ├── dashboard/           → Tableau de bord et transferts récents
│   ├── historique/          → Historique complet avec filtres
│   ├── detail-transfert/    → Détail et timeline d'un transfert
│   └── profil/              → Profil, notifications et sécurité
└── components/
├── transfert-card/      → Carte d'un transfert dans la liste
├── statut-timeline/     → Timeline Initié → En cours → Payé
└── chatbot/             → Assistant chat flottant

---

## Stack Technologique

| Couche | Technologie |
|--------|-------------|
| Backend | Spring MVC 6 + Spring Security 6 |
| ORM | Spring Data JPA + Hibernate |
| Base de données | PostgreSQL |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Frontend | Angular 17+ + Chart.js |
| Sécurité | JWT (Access 1h + Refresh 7j) + 2FA SMS |
| Conteneurisation | Docker + Docker Compose |
| CI/CD | GitHub Actions |
| Tests | JUnit 5 + Mockito + MockMvc |

---

## Lancer le projet

```bash
# Backend
cd backend
mvn clean install
mvn tomcat7:run

# Frontend
cd frontend
npm install
ng serve
```

---

## Équipe
Projet étudiant 2025-2026