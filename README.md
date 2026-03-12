# Auth Server - TP1

## Description
Serveur d'authentification réalisé dans le cadre du cours CDWFS - TP1.
Première version volontairement non sécurisée pour comprendre les risques
de sécurité. Les failles seront corrigées dans les TP 2, 3 et 4.

Stack : Java 17, Spring Boot 3.x, MySQL, Maven

---

## Prérequis
- Java 17
- Maven 3.9
- MySQL (WAMP ou autre)

---

## Installation et lancement

1. Cloner le projet :
```bash
git clone https://github.com/steavenspr/auth-server-tp1.git
```

2. Créer la base de données MySQL :
```sql
CREATE DATABASE auth_db;
```

3. Configurer `src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auth_db
spring.datasource.username=root
spring.datasource.password=
```

4. Lancer l'application :
```bash
mvn spring-boot:run
```

5. L'API est accessible sur : http://localhost:8080

La table `users` est créée automatiquement par Hibernate au démarrage.

---

## Compte de test
- Email : toto@example.com
- Password : pwd1234

---

## Endpoints disponibles

| Méthode | URL | Description |
|---------|-----|-------------|
| POST | /api/auth/register | Créer un compte |
| POST | /api/auth/login | Se connecter |
| GET | /api/me | Voir son profil |

### Exemples avec Postman

**Inscription :**
```
POST http://localhost:8080/api/auth/register?email=toto@example.com&password=pwd1234
```

**Connexion :**
```
POST http://localhost:8080/api/auth/login?email=toto@example.com&password=pwd1234
```

**Profil (avec le token reçu au login) :**
```
GET http://localhost:8080/api/me?token=<votre-token>
```

### Codes HTTP retournés

| Code | Signification |
|------|--------------|
| 200 | Succès |
| 400 | Données invalides (email vide, mot de passe trop court) |
| 401 | Email ou mot de passe incorrect |
| 409 | Email déjà utilisé |

---

## Lancer les tests
```bash
mvn test
```

Les tests utilisent H2 (base de données en mémoire) pour s'isoler de MySQL.

---

## Logging
Les événements d'authentification sont enregistrés dans `logs/auth.log` :
- Inscription réussie / échouée
- Connexion réussie / échouée
- Les mots de passe ne sont jamais loggés

---

## Analyse de sécurité TP1

Cette implémentation est volontairement dangereuse. Voici les 5 vulnérabilités
identifiées :

### Risque 1 : Mot de passe stocké en clair
Le mot de passe est sauvegardé tel quel dans la base de données, sans aucun
chiffrement. Si quelqu'un accède à la base, il voit directement tous les mots
de passe. En production, il faudrait utiliser un algorithme de hachage comme BCrypt.

### Risque 2 : Token sans expiration
Le token généré après le login n'a pas de durée de vie. Une fois créé, il reste
valide indéfiniment. Si quelqu'un vole le token, il peut accéder au compte sans
limite de temps. En production, un token doit expirer après un certain délai.

### Risque 3 : Règles de mot de passe trop faibles
Seulement 4 caractères minimum, sans obligation de majuscules, chiffres ou
caractères spéciaux. Un mot de passe court est facile à deviner par force brute.

### Risque 4 : Pas de protection contre la force brute
Aucune limite sur le nombre de tentatives de connexion. Un attaquant peut
essayer des milliers de mots de passe sans être bloqué. En production, il
faudrait verrouiller un compte après plusieurs échecs consécutifs.

### Risque 5 : Communication non chiffrée (HTTP)
Les données (email, mot de passe, token) transitent en clair sur le réseau.
N'importe qui peut les intercepter. En production, il faut utiliser HTTPS.