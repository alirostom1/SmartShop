# SmartShop — Système de Gestion Commerciale B2B

API REST Spring Boot pour MicroTech Maroc, distributeur de matériel informatique à Casablanca.

---

## 📋 Contexte

Application web de gestion commerciale destinée à MicroTech Maroc, distributeur B2B de matériel informatique basé à Casablanca. L'application permet de gérer un portefeuille de 650 clients actifs avec un système de fidélité à remises progressives et des paiements fractionnés multi-moyens par facture.

## ✨ Fonctionnalités Principales

### 👥 Gestion des Clients
- CRUD complet des clients
- Suivi automatique des statistiques (commandes, montants cumulés)
- Dates de première et dernière commande
- Historique des commandes détaillé

### 🏆 Système de Fidélité Automatique
- **Niveaux** : BASIC, SILVER, GOLD, PLATINUM
- Calcul automatique basé sur l'historique client
- Remises progressives selon le niveau :
    - SILVER : 5% si sous-total ≥ 500 DH
    - GOLD : 10% si sous-total ≥ 800 DH
    - PLATINUM : 15% si sous-total ≥ 1200 DH

### 📦 Gestion des Produits
- CRUD des produits avec soft delete
- Gestion de stock avec réservations
- Filtrage et pagination

### 🛒 Gestion des Commandes
- Commandes multi-produits avec quantités
- Validation automatique du stock
- Calculs automatiques :
    - Sous-total HT
    - Remises (fidélité + codes promo)
    - TVA 20% sur montant après remise
    - Total TTC
- Gestion des statuts : PENDING, CONFIRMED, CANCELED, REJECTED

### 💳 Système de Paiements Multi-Moyens
- **Espèces** : Limite 20,000 DH, paiement immédiat
- **Chèque** : Paiement différé, gestion des échéances
- **Virement** : Paiement immédiat ou différé
- Paiements fractionnés par commande
- Traçabilité complète des encaissements

## 🔐 Authentification & Rôles

### Rôle ADMIN (Employé MicroTech)
- Accès complet à toutes les fonctionnalités
- Gestion des clients, produits, commandes, paiements
- Validation et annulation des commandes
- Enregistrement des paiements

### Rôle CLIENT (Entreprises clientes)
- Consultation de son propre profil
- Historique de ses commandes
- Consultation des produits (lecture seule)
- Pas de création/modification/suppression

## ⚙️ Stack Technique

- **Framework** : Spring Boot 4.0.0
- **Langage** : Java 17+
- **Base de données** : PostegresSQL
- **ORM** : Spring Data JPA / Hibernate
- **API** : REST avec JSON
- **Authentification** : Session HTTP (pas de JWT)
- **Validation** : Bean Validation (Jakarta)
- **Mapping** : MapStruct
- **Architecture** : Controller-Service-Repository-DTO
- **Tests** : JUnit 5, Mockito

## 📊 Règles Métier Critiques

1. **Validation stock** : `quantité_demandée ≤ stock_disponible`
2. **Limite espèces** : 20,000 DH maximum (Article 193 CGI)
3. **Confirmation commande** : Uniquement si totalement payée
4. **Codes promo** : Format `PROMO-XXXX`
5. **TVA** : 20% calculée sur montant APRÈS remise
6. **Arrondis** : Tous les montants à 2 décimales

## 🚀 Installation & Exécution

```bash
# 1. Cloner le projet
git clone <repository-url>
cd smartshop

# 2. Configurer la base de données
# cp .env.example .env

# 3. Compiler et exécuter
mvn clean package
java -jar target/smartshop-*.jar

# Ou exécuter directement
mvn spring-boot:run
```

## 📚 Documentation API

- **Collection Postman** : Disponible dans [lien](https://www.postman.com/voxa-team/workspace/public-collections/collection/42850483-06e6077e-fb1d-48f8-b5d7-6097c5eb418f?action=share&source=copy-link&creator=42850483)
- **Tests** : Via Postman

## 🧪 Tests

```bash
# Exécuter tous les tests
mvn test

# Exécuter avec rapport de couverture
mvn clean verify
```

## 🔄 Flux de Paiement

1. Commande créée → Statut `PENDING`
2. Paiements ajoutés (espèces/chèque/virement)
3. Paiements déposés (pour chèques)
4. Quand total déposé = total commande → Peut être confirmée
5. Commande confirmée → Statut `CONFIRMED`

---

*Application purement backend REST (API uniquement) - Pas d'interface graphique*