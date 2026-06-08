# Backend - Gestion de Caisse Agent

## 📁 Fichiers de la fonctionnalité

### ✅ DTOs (5 fichiers)
```
src/main/java/com/okanetransfer/dto/
├── CashBalanceDTO.java              ✅ Solde de caisse
├── OperationCaisseDTO.java          ✅ Opération de caisse
├── CashCloseRequestDTO.java         ✅ Requête clôture
├── CashCloseResponseDTO.java        ✅ Réponse clôture
└── DiscrepancyRequestDTO.java       ✅ Signalement écart
```

### ✅ Services (2 fichiers)
```
src/main/java/com/okanetransfer/service/
├── CashService.java                 ✅ Logique métier caisse
└── AuditService.java                ✅ Journalisation
```

### ✅ Controller (1 fichier)
```
src/main/java/com/okanetransfer/controller/
└── AgentCashController.java         ✅ Endpoints REST
```

### ✅ Exceptions (2 fichiers)
```
src/main/java/com/okanetransfer/exception/
├── CashDrawerAlreadyClosedException.java  ✅ Caisse déjà clôturée
└── GlobalExceptionHandler.java            ✅ Gestionnaire global
```

### ✅ Tests (2 fichiers)
```
src/test/java/com/okanetransfer/
├── service/CashServiceTest.java           ✅ 8 tests
└── controller/AgentCashControllerTest.java ✅ 8 tests
```

## 🎯 Endpoints API

### 1. GET /api/agent/cash/balance
Consulter le solde de caisse

**Réponse:**
```json
{
  "solde": 10500.00,
  "devise": "MAD"
}
```

### 2. GET /api/agent/cash/operations?date=2026-06-07
Consulter les opérations d'une journée

**Réponse:**
```json
[
  {
    "id": 1,
    "type": "ENCAISSEMENT",
    "montant": 1000.00,
    "soldeApres": 11500.00,
    "date": "2026-06-07T10:00:00",
    "referenceTransfertId": 5,
    "description": "Encaissement envoi"
  }
]
```

### 3. POST /api/agent/cash/close
Clôturer la caisse journalière

**Requête:**
```json
{
  "soldeReelSaisi": 10450.00
}
```

**Réponse:**
```json
{
  "soldeTheorique": 10500.00,
  "soldeReel": 10450.00,
  "ecart": -50.00,
  "message": "Clôture effectuée avec écart"
}
```

### 4. POST /api/agent/cash/discrepancy
Signaler un écart de caisse

**Requête:**
```json
{
  "ecartConstate": -50.00,
  "commentaire": "Manque 50 MAD"
}
```

## 🔒 Sécurité

- ✅ JWT authentification requise
- ✅ Rôle ROLE_AGENT requis
- ✅ Audit de toutes les actions sensibles
- ✅ Validation des données (@Valid)

## 🗄️ Tables Base de Données

### Utilisées
- `cash_drawers` - Caisses des agents
- `cash_operations` - Opérations de caisse
- `discrepancy_reports` - Signalements d'écarts
- `audit_logs` - Journal d'audit
- `users` - Utilisateurs/agents
- `agencies` - Agences

## ⚙️ Installation

### 1. Script SQL
Exécuter le script de migration :
```bash
psql -U postgres -d okane_db -f src/main/resources/db/migration/V3__agent_features.sql
```

### 2. Compilation
```bash
mvn clean install
```

### 3. Tests
```bash
mvn test
```

### 4. Déploiement
```bash
mvn package
# Le WAR sera dans target/okane-transfer-1.0-SNAPSHOT.war
```

## 🧪 Tests Rapides

### 1. Login
```bash
curl -X POST http://localhost:8080/okane/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"agent@okane.com","password":"password123"}'
```

### 2. Consulter Solde
```bash
curl -X GET http://localhost:8080/okane/api/agent/cash/balance \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Clôturer Caisse
```bash
curl -X POST http://localhost:8080/okane/api/agent/cash/close \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"soldeReelSaisi": 10450.00}'
```

## 📊 Règles Métier

1. **Solde**: Consulté en temps réel depuis la BD
2. **Opérations**: Filtrées par date (défaut: aujourd'hui)
3. **Clôture**: 
   - Une seule clôture par jour autorisée
   - Calcul automatique de l'écart
   - Mise à jour du solde avec le réel saisi
4. **Écart**: Signalement sans modification du solde

## ✅ Checklist

- [ ] Base de données configurée
- [ ] Tables créées (cash_operations, discrepancy_reports, audit_logs)
- [ ] Application compilée
- [ ] Tests passent (16 tests)
- [ ] Caisse créée pour l'agent de test
- [ ] Endpoints testés avec Postman/cURL

## 🐛 Dépannage

### Erreur: "Aucune caisse trouvée"
```sql
INSERT INTO cash_drawers (agent_id, agency_id, balance, status, opening_time)
VALUES (1, 1, 10000.00, 'OPEN', CURRENT_TIMESTAMP);
```

### Erreur: "Caisse déjà clôturée"
C'est normal, une seule clôture par jour autorisée.

## 📚 Documentation API

Swagger UI disponible à:
```
http://localhost:8080/okane/swagger-ui.html
```

---

**Version:** 1.0.0  
**Statut:** ✅ Prêt pour intégration
