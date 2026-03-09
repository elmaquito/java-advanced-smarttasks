# 🚀 Guide de Déploiement & Démarrage

Ce guide explique comment lancer l'environnement complet SmartTasks (Backend + Base de Données + MinIO + Frontend).

## 📋 Prérequis

- **Java 21** (JDK)
- **Docker** & **Docker Compose**
- **Node.js** (v18+)
- **Git**

## 1. Configuration de l'Environnement

Le projet utilise des variables d'environnement pour les secrets (Base de données, OAuth Google).
Des fichiers `.env` ont été exclus du contrôle de version pour la sécurité.

### Backend & Infrastructure (`.env`)
Créez un fichier `.env` à la racine du projet (`java-advanced-smarttasks/`) :

```dotenv
# Database Configuration
POSTGRES_USER=smart
POSTGRES_PASSWORD=smart
POSTGRES_DB=smarttasks
POSTGRES_PORT=5432
POSTGRES_HOST=localhost

# Minio Configuration
MINIO_ROOT_USER=admin
MINIO_ROOT_PASSWORD=password
MINIO_URL=http://localhost:9000
MINIO_BUCKET=smarttasks

# API Configuration
API_PORT=8080
VITE_API_BASE_URL=http://localhost:8080

# Google OAuth Configuration
# Obtenez ces clés sur la console Google Cloud
VITE_GOOGLE_CLIENT_ID=votre-client-id-google
VITE_GOOGLE_CLIENT_SECRET=votre-client-secret-google
GOOGLE_ISSUER_URI=https://accounts.google.com
```

### Frontend (`projet-front/.env`)
Créez un fichier `.env` dans le dossier `projet-front/` :

```dotenv
VITE_GOOGLE_CLIENT_ID=votre-client-id-google
VITE_GOOGLE_CLIENT_SECRET=votre-client-secret-google
VITE_API_BASE_URL=http://localhost:8080
```

## 2. Démarrage de l'Infrastructure (Docker)

Lancez la base de données PostgreSQL et le serveur MinIO :

```bash
# À la racine du projet
docker-compose up -d
```

Vérifiez que les conteneurs tournent :
- **PostgreSQL** : port 5432
- **MinIO Console** : port 9001 (User: admin / Pass: password)
- **MinIO API** : port 9000

## 3. Démarrage du Backend (Spring Boot)

```bash
cd projet-back/demo
# Linux / Mac
./mvnw spring-boot:run
# Windows
.\mvnw.cmd spring-boot:run
```

L'API sera accessible sur `http://localhost:8080`.
Documentation Swagger : `http://localhost:8080/swagger-ui/index.html`

## 4. Démarrage du Frontend (React + Vite)

```bash
cd projet-front
npm install
npm run dev
```

L'application sera accessible sur `http://localhost:5173`.

## 🆘 Dépannage

### Erreur de connexion BDD
- Vérifiez que le conteneur Docker PostgreSQL est UP (`docker ps`).
- Vérifiez les identifiants dans le fichier `.env`.

### Erreur OAuth Google
- Assurez-vous que l'URL `http://localhost:5173` est bien ajoutée aux "Origines JavaScript autorisées" dans votre console Google Cloud.

### Clean Build
Si vous rencontrez des erreurs de compilation Java :
```bash
cd projet-back/demo
./mvnw clean install -DskipTests
```
