# 🎬 Guide de Démonstration Vidéo - SmartTasks

Ce guide détaille les étapes à suivre pour réaliser une vidéo de démonstration complète du projet SmartTasks, mettant en avant les fonctionnalités clés et les correctifs récents.

## 🛠️ Prérequis avant l'enregistrement

1.  **Vérifier l'environnement** :
    - Docker Desktop est lancé (PostgreSQL et MinIO actifs).
    - Le Backend Spring Boot tourne sur le port `8080`.
    - Le Frontend React tourne sur le port `5173`.
2.  **Préparer les onglets** :
    - **Onglet 1** : L'application Frontend (http://localhost:5173).
    - **Onglet 2** : La console MinIO (http://localhost:9001) - Login: `admin` / `password`.
    - **Onglet 3** : Votre IDE (VS Code) ou Terminal pour les commandes.

---

## 📹 Scénario de la Démonstration (Durée estimée : 2-3 min)

### 1️⃣ Introduction et Création de Projet (Standard Use Case)
*Objectif : Montrer que l'application de base fonctionne.*

1.  **Action** : Connectez-vous à l'application avec un compte utilisateur.
2.  **Action** : Sur le Dashboard, cliquez sur **"Create Project"**.
3.  **Action** : Nommez le projet (ex: "Démo Vidéo Mars") et validez.
4.  **Vérification** : Le projet apparaît dans la liste. Cliquez dessus pour entrer dans le détail.
   > 🗣️ *Script suggéré : "Nous commençons par créer un projet standard. L'interface charge désormais correctement les détails du projet sans erreur."*

### 2️⃣ Gestion des Tâches et Fichiers (Fonctionnalité Corrigée)
*Objectif : Prouver que le bug d'upload et d'affichage est résolu.*

1.  **Action** : Dans le projet, créez une nouvelle tâche (ex: "Rapport Final").
2.  **Action** : Cliquez sur la tâche pour voir ses détails.
3.  **Action** : Dans la section "Attachments", cliquez sur **"Choose File"**.
4.  **Action** : Sélectionnez un fichier (image ou PDF > 1Mo si possible pour montrer la robustesse, ou un fichier texte simple).
5.  **Action** : Cliquez sur **"Upload"**.
6.  **Vérification** : Le fichier apparaît dans la liste.
7.  **Action** : Cliquez sur le lien **"Download"** pour prouver que le fichier est bien accessible.
   > 🗣️ *Script suggéré : "Voici la fonctionnalité de gestion des fichiers. Nous pouvons uploader des pièces jointes, même volumineuses (support jusqu'à 10Mo), et les télécharger instantanément."*

### 3️⃣ Vérification Infrastructure (MinIO)
*Objectif : Montrer que le stockage est réel et découplé.*

1.  **Action** : Basculez sur l'onglet **MinIO Console**.
2.  **Action** : Allez dans le bucket `smarttasks`.
3.  **Vérification** : Montrez que le fichier que vous venez d'uploader est bien présent ici.
   > 🗣️ *Script suggéré : "Côté infrastructure, les fichiers sont stockés de manière sécurisée dans notre bucket MinIO, assurant la persistance des données."*

### 4️⃣ Isolation Multi-Tenant (Feature Avancée)
*Objectif : Démontrer l'architecture SaaS et la sécurité des données.*

1.  **Action** : Retournez sur le Frontend et notez l'ID du projet créé (ex: Projet #5).
2.  **Action** : Ouvrez votre outil de base de données ou terminal.
3.  **Action** : Exécutez une commande SQL pour changer le `tenant_id` de ce projet :
    ```sql
    UPDATE projects SET tenant_id = 'autre_client' WHERE id = 5;
    ```
    *(Remplacez 5 par l'ID réel de votre projet)*
4.  **Action** : Retournez sur le Frontend et actualisez la page "Projects".
5.  **Vérification** : Le projet a **disparu** de la liste (car vous êtes connecté avec le tenant par défaut).
6.  **Action (Optionnel)** : Remettez le tenant à 'default' pour le faire réapparaître.
   > 🗣️ *Script suggéré : "Enfin, nous démontrons l'isolation Multi-Tenant. En changeant l'ID client en base de données, le projet devient invisible pour l'utilisateur actuel, garantissant que chaque client ne voit que ses propres données."*

---

## ✅ Conclusion
Terminez la vidéo en montrant le Dashboard récapitulatif fonctionnel.
