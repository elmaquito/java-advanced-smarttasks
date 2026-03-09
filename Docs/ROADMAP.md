# 🗺️ Roadmap du Projet SmartTasks

Ce document trace la route pour la finalisation du backend et son évolution vers une architecture robuste et scalable.

## 🏁 Phase 1 : Stabilisation & Documentation (Module 05) - *En cours*
**Objectif :** Rendre l'API consommable par le frontend et les développeurs tiers.

- [x] **Intégration Swagger/OpenAPI** : Ajout de la dépendance et configuration de base.
- [x] **Documentation des Endpoints** : Annotation des contrôleurs (`Attachment`, `Project`, `Task`) avec `@Operation` et `@ApiResponse`.
- [x] **Configuration CORS** : Autoriser le frontend (`localhost:5173`) à communiquer avec le backend.
- [ ] **Vérification Intégration Front** : Valider que le frontend React affiche correctement les données (tâches, projets, fichiers).

## 🏗️ Phase 2 : Refactoring Clean Architecture (Module 06) - *Prochaine étape*
**Objectif :** Découpler le code métier (Domain) du framework et de la base de données pour améliorer la maintenabilité.

- [ ] **Création des packages** : Structurer en `domain`, `application`, `infrastructure`.
- [ ] **Extraction du Domaine** :
    - Créer des objets métier purs (POJOs/Records) sans annotations JPA (`@Entity`).
    - Définir des **Ports** (interfaces) pour les repositories (ex: `TaskRepositoryPort`).
- [ ] **Couche Application** :
    - Déplacer la logique des Services actuels vers des `UseCases` (ex: `CreateTaskUseCase`).
- [ ] **Couche Infrastructure** :
    - Implémenter les Adaptateurs (JPA Repository, Minio Client) qui implémentent les Ports du domaine.
    - Mapper les Entités JPA vers les Objets du Domaine.

## 🚀 Phase 3 : Architecture Distribuée (Module 07) - *Vision cible*
**Objectif :** Préparer le passage à l'échelle (Scaling).

- [ ] **Analyse des bornes contextuelles** : Identifier les sous-domaines (Projets vs Fichiers vs Utilisateurs).
- [ ] **Découpage théorique** :
    - Service `Task-Core` (Gestion des tâches).
    - Service `File-Storage` (Pour MinIO).
    - Service `Identity` (Pour l'auth).
- [ ] **Architecture** : Introduction d'une **API Gateway** pour un point d'entrée unique.

## 📝 Actions Immédiates Recommandées

1.  **Valider le cycle complet Front-Back** : Ouvrir l'application React et tester le flux complet (Créer projet -> Créer tâche -> Uploader fichier -> Télécharger).
2.  **Lancer le chantier Clean Architecture** : Commencer par extraire le domaine `Task` pour s'entraîner sur une petite partie avant de tout refondre.
