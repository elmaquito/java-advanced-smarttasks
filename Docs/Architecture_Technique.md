# 🏗️ Architecture Technique - SmartTasks

Ce document détaille les choix architecturaux structurant le backend de SmartTasks.

## 1. Vue d'Ensemble : Clean Architecture (Hexagonale)

Le projet suit les principes de la **Clean Architecture** (ou Architecture Hexagonale/Ports & Adapters). L'objectif est d'isoler le code métier (cœur de l'application) des détails techniques (Frameworks, BDD, API tierces).

### Structure des Packages
Pour chaque module métier (ex: `Project`, `Task`), la structure est la suivante :

```
fr.limayrac.demo.<module>
├── domain              # 🟢 Cœur Métier (Indépendant de tout framework)
│   └── model           # Objets métier purs (POJO)
├── application         # 🟡 Cas d'Utilisation (Orchestration)
│   ├── port            
│   │   ├── in          # Interfaces d'entrée (Services/UseCases)
│   │   └── out         # Interfaces de sortie (Repositories/Gateways)
│   └── service         # Implémentation de la logique applicative
└── infrastructure      # 🔴 Détails Techniques (Framework driven)
    ├── persistence     # Base de Données (JPA)
    │   ├── entity      # Entités Hibernate (@Entity)
    │   ├── repository  # Interfaces Spring Data JPA
    │   └── adapter     # Implémentation des ports de sortie (Port -> Adapter)
    └── web             # Contrôleurs REST (@RestController)
```

## 2. Modules & Contextes Bornés (Bounded Contexts)

L'application est divisée en modules distincts, préfigurant un découpage en microservices.

### Module `Project`
Gère la création et la configuration des projets.
- **Responsabilité** : Cycle de vie des projets.

### Module `Task`
Gère les tâches associées aux projets.
- **Responsabilité** : Création, mise à jour, suivi des tâches.
- **Découplage** :
    - Ce module ne dépend pas directement des classes du module `Project`.
    - La relation BDD `Project <-> Task` n'utilise plus de foreign key stricte au niveau objet JPA (`@OneToMany`).
    - **ProjectGateway** : Une interface (Port) dans le module `Task` permet de "demander" des informations au module `Project`. Son implémentation (Adapter) se trouve dans l'infrastructure et fait le pont.

## 3. Gestion de la Donnée & Multi-Tenancy

L'application est **Multi-Tenant** (Soft Isolation). Toutes les données résident dans la même base, mais sont cloisonnées logiquement.

### Mécanisme
1.  **Requête Entrante** : Un filtre (`TenantFilter`) intercepte chaque requête HTTP.
2.  **Extraction** : Il extrait le header `X-Tenant-ID` (ou l'utilisateur connecté).
3.  **Contexte** : L'ID du tenant est stocké dans un `ThreadLocal` via `TenantContext`.
4.  **Persistance** : Hibernate utilise ce contexte pour filtrer automatiquement les requêtes (via `@Filter` ou logique applicative dans les Repositories), assurant qu'un utilisateur ne voit que les données de son organisation.

## 4. Sécurité (OAuth2)

- **Protocole** : OAuth2 / OpenID Connect.
- **Provider** : Google (configuré dans `.env`).
- **Implémentation** : Spring Security Resource Server.
- **Token** : Le backend valide les JWT (Json Web Tokens) envoyés par le frontend dans le header `Authorization: Bearer ...`.

## 5. Stockage de Fichiers (MinIO)

Les fichiers attachés aux tâches sont stockés dans un Object Storage compatible S3.
- **Local** : MinIO (lancé via Docker Compose).
- **Production** : Compatible avec AWS S3, Google Cloud Storage, etc.
- **Architecture** : Le service `MinioService` implémente la logique de stockage, découplée du métier.
