# 🗺️ Roadmap du Projet SmartTasks

Ce document trace la route pour la finalisation du backend et son évolution vers une architecture robuste et scalable.

## 🏁 Phase 1 : Stabilisation & Documentation (Module 05) - *Terminé* ✅
**Objectif :** Rendre l'API consommable par le frontend et les développeurs tiers.

- [x] **Intégration Swagger/OpenAPI** : Ajout de la dépendance et configuration de base.
- [x] **Documentation des Endpoints** : Annotation des contrôleurs (`Attachment`, `Project`, `Task`) avec `@Operation` et `@ApiResponse`.
- [x] **Configuration CORS** : Autoriser le frontend (`localhost:5173`) à communiquer avec le backend.
- [x] **Vérification Intégration Front** : Valider que le frontend React affiche correctement les données (tâches, projets, fichiers).

## 🏗️ Phase 2 : Refactoring Clean Architecture (Module 06) - *Terminé* ✅
**Objectif :** Découpler le code métier (Domain) du framework et de la base de données pour améliorer la maintenabilité.

- [x] **Création des packages** : Structuration en `domain`, `application`, `infrastructure`.
- [x] **Extraction du Domaine** :
    - Création des objets métier purs (POJOs/Records) sans annotations JPA (`@Entity`).
    - Définition des **Ports** (interfaces) pour les repositories (ex: `TaskRepositoryPort`).
- [x] **Couche Application** :
    - Déplacement de la logique des Services vers des `UseCases` (ex: `CreateTaskUseCase`).
- [x] **Couche Infrastructure** :
    - Implémentation des Adaptateurs (JPA Repository, Minio Client) implémentant les Ports du domaine.
    - Mapping des Entités JPA vers les Objets du Domaine.

## 🚀 Phase 3 : Architecture Distribuée (Module 07) - *Terminé* ✅
**Objectif :** Préparer le passage à l'échelle (Scaling) et découpler les contextes bornés.

- [x] **Analyse des bornes contextuelles** : Identification des sous-domaines (`Project`, `Task`).
- [x] **Découpage Modulaire** :
    - Séparation physique des modules `Project` et `Task` dans des packages distincts.
    - Suppression des relations directes JPA (`@OneToMany`) entre modules.
    - Introduction de `ProjectGateway` pour la communication inter-modules.
- [x] **Découpage théorique** :
    - Service `Task-Core` (Gestion des tâches).
    - Service `File-Storage` (Pour MinIO).
    - Service `Identity` (Pour l'auth).

## 🔒 Phase 4 : Sécurité & Livraison - *Terminé* ✅
**Objectif :** Finaliser la sécurité et préparer les livrables.

- [x] **Authentification OAuth2** : Mise en place du Resource Server avec validation JWT Google.
- [x] **Multi-Tenancy** : Isolation des données par `TenantContext` et filtres Hibernate.
- [x] **Nettoyage Git** : Suppression des secrets (Google Client ID/Secret) de l'historique Git.
- [x] **Documentation** : Rédaction des guides d'architecture et de déploiement.

## 📝 État Final

Le projet est désormais un **Monolithe Modulaire** prêt à être découpé en microservices si nécessaire. Le code métier est isolé, testé unitairement, et la dette technique liée au couplage fort JPA a été résolue.
