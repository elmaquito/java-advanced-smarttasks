# 📄 Analyse Technique & Rétrospective : Projet SmartTasks

**Date** : 10 Mars 2026  
**Projet** : SmartTasks (Gestion de Projets & Tâches)  
**Version** : Phase 4 (Architecture Distribuée & Clean Architecture)  

---

## 1. Synthèse Exécutive

SmartTasks est une application full-stack (Java/Spring Boot + React/TypeScript) conçue pour la gestion collaborative de projets. Le projet a évolué d'une architecture monolithique en couches vers une **Architecture Hexagonale (Clean Architecture)** modulaire, préparant le terrain pour une migration potentielle vers des microservices. Ce document détaille les choix techniques, la structure modulaire et les solutions apportées aux défis de conception.

---

## 2. Chronologie & Évolution Technique

Le développement du backend a suivi une méthodologie itérative rigoureuse, chaque module ajoutant une complexité structurelle :

### 🟡 Phase 1 : Monolithe "Layered" (Classique)
*   **Approche** : Architecture MVC standard (Controller → Service → Repository → Entity).
*   **Limitations** : Couplage fort entre la logique métier et le framework de persistance (Hibernate). Les entités JPA "fuitaient" dans toute l'application.
*   **Fonctionnalité** : CRUD basique pour Projets et Tâches.

### 🟠 Phase 2 : Sécurisation & Externalisation
*   **Sécurité** : Implémentation d'un serveur de ressources OAuth2 (**Spring Security 6**) déléguant l'authentification à un fournisseur tiers (Google).
*   **Stockage** : Abandon du stockage de fichiers en base de données (BLOB) au profit d'un stockage objet compatible S3 via **MinIO**, améliorant la performance et la scalabilité.

### 🟢 Phase 3 : Refactoring "Clean Architecture"
*   **Objectif** : Inversion des dépendances pour isoler le Domaine.
*   **Transformation** :
    *   **Domain** : classes POJO pures (ex: `Project`) sans annotations framework.
    *   **Ports (Interfaces)** : Définition des contrats d'entrée (`UseCase`) et de sortie (`RepositoryPort`).
    *   **Adapters** : Implémentation technique (JPA, Rest Controllers).

### 🔵 Phase 4 : Modularisation & Découplage (Bounded Contexts)
*   **Stratégie** : Rupture des liens forts (Clés étrangères JPA) entre les modules `Project` et `Task`.
*   **Communication** : Introduction de **Gateways** applicatifs pour permettre au module `Task` de consommer des données de `Project` sans dépendance directe au niveau de la base de données.

---

## 3. Analyse Architecturelle Détaillée

L'architecture actuelle respecte les principes de séparation des préoccupations :

### A. Le Noyau (Domain & Application)
Le cœur de l'application est agnostique du framework.
*   **Domain Model** : Objets riches contenant la logique métier et l'état.
    *   *Exemple* : `fr.limayrac.demo.project.domain.model.Project` (Simple Java Object).
*   **Application Layer (Use Cases)** : Orchestration des flux métier.
    *   *Rôle* : Coordonne les actions (créer un projet, assigner une tâche) en utilisant le domaine.
    *   *Interface* : `ProjectUseCase` définit les opérations disponibles.

### B. L'Infrastructure (Adapters)
Ces couches s'adaptent aux technologies externes.
*   **Persistence Adapter** : Traduit les objets du domaine en entités JPA (`@Entity`) pour PostgreSQL.
    *   *Pattern* : Utilise des **Mappers** pour convertir `Project` ↔ `ProjectEntity`.
*   **Web Adapter** : Expose les Use Cases via une API REST.
    *   *Outil* : Spring WebMVC controllers + DTOs (Data Transfer Objects) pour découpler l'API du modèle interne.

---

## 4. Modules Fonctionnels & Techniques

### Module `Project`
*   **Responsabilité** : Cycle de vie des projets, configuration des droits d'accès.
*   **Particularité** : Structure "Pure Clean Arch" complète.

### Module `Task`
*   **Responsabilité** : Gestion opérationnelle (Todo, Doing, Done).
*   **Découplage** : Ne possède plus de relation `@ManyToOne Project` directe. Elle stocke uniquement un `projectId` (référence logique).
*   **Intégration** : Utilise un `ProjectGateway` (Port) pour vérifier l'existence d'un projet avant de créer une tâche.

### Module `Attachment` (MinIO)
*   **Technique** : Client MinIO pour l'interaction S3.
*   **Flux** : Upload → Stockage S3 → Génération d'URL présignée ou publique → Sauvegarde des métadonnées en BDD.

---

## 5. Stack Technique & Infrastructure

### Backend
| Composant | Technologie | Version | Rôle |
| :--- | :--- | :--- | :--- |
| **Langage** | Java | 21 LTS | Socle d'exécution |
| **Framework** | Spring Boot | 3.4.1 | Injection de dépendances, Web, Data |
| **Base de Données** | PostgreSQL | 15+ | Persistance relationnelle |
| **ORM** | Hibernate (Spring Data JPA) | 6.x | Mapping Objet-Relationnel |
| **Sécurité** | Spring Security (OAuth2) | 6.x | Authentification & Autorisation |
| **Stockage Fichiers** | MinIO | LATEST | Object Storage (S3 Compatible) |
| **Documentation** | SpringDoc OpenAPI | 2.x | Documentation API (Swagger UI) |
| **Build** | Maven | 3.9+ | Gestion des dépendances |

### Frontend
| Composant | Technologie | Rôle |
| :--- | :--- | :--- |
| **Framework** | React | 18.3 | Bibliothèque UI |
| **Langage** | TypeScript | 5.x | Typage statique |
| **Build Tool** | Vite | 5.x | Bundler rapide et serveur de dev |
| **Composants** | Shadcn/UI + Radix | | Composants accessibles et stylisés |
| **État Serveur** | TanStack Query | 5.x | Gestion du cache et des requêtes API |

---

## 6. Problématiques Rencontrées & Solutions

### 🔴 Problème 1 : Couplage Fort JPA
*   **Symptôme** : Impossible de tester la logique métier sans lancer une base de données H2/Postgres. Modification de la BDD impactant toute l'app.
*   **Solution** : **Mappers**. Création d'une frontière stricte. Le repository Infrastructure convertit les entités BDD en objets Domaine avant de les renvoyer au Service.

### 🔴 Problème 2 : Gestion des Fichiers Lourds
*   **Symptôme** : Ralentissement de la base de données lors du stockage d'images/PDF en `byte[]` dans les tables.
*   **Solution** : **Externalisation vers MinIO**. La base de données ne stocke que les métadonnées (nom, taille, type MIME, URL), le fichier binaire est déchargé sur le serveur de stockage objet.

### 🔴 Problème 3 : Dépendances Cycliques entre Modules
*   **Symptôme** : `ProjectService` a besoin de `TaskService` (pour supprimer les tâches d'un projet) et `TaskService` a besoin de `ProjectService` (pour vérifier le projet).
*   **Solution** : **Inversion de dépendance & Events**. Utilisation d'interfaces (Gateway) définies dans le module consommateur et implémentée dans l'infrastructure, ou usage d'événements applicatifs (`ApplicationEventPublisher`) pour découpler les actions destructives.

---

## 7. Configuration & Environnement

Le fichier `application.yaml` centralise la configuration :
*   **Base de données** : Configurée sur `jdbc:postgresql://localhost:5432/smarttasks`.
*   **JPA** : `ddl-auto: update` pour le développement rapide (schéma évolutif).
*   **MinIO** : Endpoints et identifiants configurables via variables d'environnement (`MINIO_URL`, etc.).

---
*Document généré automatiquement par GitHub Copilot pour l'analyse technique du repository java-advanced-smarttasks.*
