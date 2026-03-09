# Rapport Technique : Implémentation Backend et Résolution d'Incidents

**Date** : 19 Février 2026  
**Projet** : SmartTasks (Backend Spring Boot)  
**Module** : `projet-back/demo`

## 1. Contexte & Objectifs

L'objectif de cette session était l'implémentation de la couche persistance et service pour la gestion des Tâches (`Task`) et Projets (`Project`) dans une architecture Clean/Hexagonale simplifiée, ainsi que la validation par tests unitaires et d'intégration.

## 2. Implémentations Techniques

### A. Modélisation du Domaine (Domain Layer)
Nous avons enrichi le modèle de données pour supporter les relations et les besoins métier :

1.  **Entités JPA (`/domain`)** :
    *   **`Project`** : Ajout de la relation `@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)` pour gérer la liste des tâches.
    *   **`Task`** : Configuration de la relation `@ManyToOne(fetch = FetchType.LAZY)` vers `Project` pour éviter le chargement inutile de données (N+1 problem).
    *   **`Attachment`** : Création d'une nouvelle entité pour stocker les métadonnées de fichiers (taille, mime-type, clé S3).

2.  **Projections & DTOs (`/dto`)** :
    *   Mise en place du pattern **Interface-based Projections** avec `TaskListResponse` et `ProjectListResponse`.
    *   **Avantage** : Permet à Hibernate/Spring Data de ne sélectionner que les colonnes nécessaires (ex: exclure la description lourde d'une liste sommaire).

### B. Couche de Persistance (Repository Layer)
*   **`TaskRepository`** :
    *   Ajout de la méthode `findAllByProjectId(Long projectId, Pageable pageable)`.
    *   Utilisation de la projection `TaskListResponse` pour optimiser les performances de lecture.

### C. Couche Service (Service Layer)
*   **`TaskService`** :
    *   Implémentation de `create(Long projectId, TaskCreateRequest request)` : Vérifie l'existence du projet avant création.
    *   Implémentation de `findAllByProjectId(...)` : Retourne une page de DTOs.

## 3. Stratégie de Tests

Une suite de tests a été mise en place pour garantir la robustesse :

*   **`ProjectRepositoryTest` (@DataJpaTest)** : Valide la persistance et le remplissage automatique des dates (`@PrePersist`).
*   **`TaskRepositoryTest` (@DataJpaTest)** : Vérifie que le `EntityManager` gère correctement les relations et que les projections fonctionnent.
*   **`TaskServiceTest` (Mockito)** : Teste la logique métier en isolant la base de données (Mock des repositories).

## 4. Résolution d'Incidents Critiques (Troubleshooting)

### Incident Majeur : `NoClassDefFoundError: TestEntityManager`

*   **Symptôme** : Lors de l'exécution de `mvn test`, les tests échouaient avec une erreur indiquant que la classe `TestEntityManager` était introuvable, bien que la dépendance `spring-boot-starter-test` soit présente.
*   **Analyse** :
    *   L'inspection de l'arbre de dépendances (`mvn dependency:tree`) et du classpath a révélé l'utilisation de la version **4.0.2** de Spring Boot.
    *   **Cause** : La version 4.0.2 n'existe pas (version stable actuelle : branche 3.x). Maven a résolu des artifacts invalides ou incomplets.
*   **Correction** :
    *   Downgrade de la version dans `pom.xml` vers **3.4.1**.
    *   Nettoyage du projet (`mvn clean`).

### Incident Mineur : Erreurs de Compilation dans les Tests

*   **Symptôme** : Après la correction du build, des erreurs `cannot find symbol` apparaissaient pour `List`, `Optional`, et `assertThat`.
*   **Correction** : Ajout des imports manquants (`java.util.*`, `org.assertj.core.api.Assertions.assertThat`).

### Incident Runtime : Connexion Base de Données (En cours)

*   **Symptôme** : L'application refuse de démarrer avec une erreur de connexion JDBC (`Connection refused` sur le port 5432).
*   **Analyse** : Le conteneur Docker PostgreSQL est actif, mais la configuration locale tente peut-être de se connecter sans les bons identifiants ou le bon réseau.
*   **Solution Temporaire** : Passage de la base de données H2 de `scope: test` à `scope: runtime` pour permettre le démarrage en mode dev sans dépendance Docker forte.

## 5. Commandes Utilisées (Historique)

```bash
# Analyse des dépendances
.\mvnw dependency:tree
.\mvnw dependency:build-classpath -Dmdep.outputFile=cp.txt

# Exécution des tests
.\mvnw clean test

# Gestion Docker
docker-compose up -d --remove-orphans
docker-compose down -v

# Lancement Application (Tentatives)
.\mvnw spring-boot:run
java -jar target/demo-0.0.1-SNAPSHOT.jar

# Gestion des Ports (Kill process sur 8080)
netstat -ano | findstr :8080
Stop-Process -Id <PID> -Force
```

## 6. Conclusion

L'implémentation backend est fonctionnelle et testée unitairement. L'infrastructure de build a été réparée en corrigeant la version du framework. Le prochain défi est la stabilisation de l'environnement d'exécution (Docker/Postgres) pour l'intégration avec le Frontend.
