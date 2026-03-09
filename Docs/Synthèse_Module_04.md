# Synthèse et Vulgarisation - Module 04 (Gestion de Fichiers avec MinIO)

## 1. Ce que nous avons réalisé (Vulgarisation)

Imaginez que notre application de gestion de tâches (*SmartTasks*) souhaite permettre aux utilisateurs d'ajouter des pièces jointes (PDF, images, etc.) à leurs tâches, exactement comme vous le feriez dans un email.

Pour réaliser cela techniquement, nous avons mis en place une architecture hybride, comparable à une bibliothèque :

*   **Le Fichier (Le Livre)** : Il est stocké sur des "étagères" robustes et extensibles. Nous utilisons **MinIO** (un clone de Amazon S3) pour cela. C'est un système fait pour stocker des objets lourds (fichiers) efficacement.
*   **Les Métadonnées (La Fiche Catalogue)** : Pour retrouver le fichier, savoir comment il s'appelle, sa taille, ou à quelle tâche il est lié, nous gardons une "fiche" dans notre base de données classique (**PostgreSQL**).

### Pourquoi séparer les deux ?
Stocker des gros fichiers directement dans la base de données (PostgreSQL) la ralentirait énormément. C'est comme essayer de ranger des encyclopédies dans votre portefeuille. On met l'argent (données légères) dans le portefeuille, et les livres (données lourdes) sur l'étagère.

## 2. État Actuel

La fonctionnalité est **implémentée et testée**.

*   **Plaçage du décor (Configuration)** : Nous avons installé le "téléphone" pour parler à MinIO (dépendance Maven) et configuré le numéro à appeler (`localhost:9000`).
*   **Le Cerveau (Service)** : `AttachmentService` s'occupe de la coordination. Quand un utilisateur envoie un fichier :
    1.  Il l'envoie à MinIO pour stockage.
    2.  Il note les détails dans PostgreSQL.
*   **Le Guichet (Controller)** : Nous avons ouvert deux guichets (API) :
    *   Un pour déposer (`POST /upload`).
    *   Un pour récupérer (`GET /download`).
*   **Sécurité & Performance** :
    *   Le téléchargement se fait en "Streaming". Le serveur ne charge pas tout le fichier en mémoire (RAM) avant de vous l'envoyer (ce qui ferait planter le serveur si le fichier fait 2 Go), mais il fait passer les données comme un tuyau continu.
    *   L'isolation des données (Multi-tenancy) est respectée : un client ne voit que ses propres fichiers.

## 3. Difficultés Rencontrées et Solutions

### A. La confusion des Ports (9000 vs 9001)
*   **Problème** : MinIO possède deux portes. Une pour les humains (la console web d'administration en 9001) et une pour les robots/code (l'API en 9000).
*   **Solution** : Nous avons configuré l'application pour parler strictement sur le port **9000**. Une erreur ici empêchait toute connexion.

### B. Tester sans le "Vrai" MinIO
*   **Problème** : Comment vérifier que notre code fonctionne automatiquement (Tests Unitaires) sans avoir besoin de lancer le vrai serveur MinIO à chaque fois ?
*   **Solution** : Nous avons utilisé des **Mocks** (des doublures). Nous avons créé un "faux" MinIO dans le test qui dit "Oui, j'ai bien reçu le fichier" sans rien faire réellement. Cela permet de valider notre logique (le code Java) indépendamment de l'infrastructure (le serveur de stockage).

### C. Liaison entre Tâches et Fichiers
*   **Problème** : Notre `TaskService` ne renvoyait que des résumés de tâches (DTO), mais pour attacher un fichier en base de données, nous avions besoin de l'objet Tâche réel (Entity).
*   **Solution** : Nous avons ajouté une méthode interne (`findEntityById`) pour faire le pont technique, tout en gardant l'API publique propre.

## Conclusion
Le module est prêt. Le code est robuste, gère les erreurs potentielles (comme un fichier non trouvé), et est optimisé pour ne pas saturer la mémoire du serveur.
