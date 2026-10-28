# LegalAssist Madagascar AI Offline

## Présentation du projet

LegalAssist Madagascar AI Offline est une application desktop développée en Java pour faciliter la recherche et l'analyse de dossiers juridiques à Madagascar.

Le projet repose sur une idée simple : proposer un outil capable d'aider les utilisateurs à organiser leurs dossiers, à identifier les domaines juridiques concernés et à retrouver plus facilement les articles de loi susceptibles de correspondre à une situation donnée.

L'application fonctionne localement, avec une base de données PostgreSQL et un système d'analyse de texte qui ne nécessite pas de connexion à un service d'intelligence artificielle en ligne.

LegalAssist a été conçu avec une architecture organisée et un code accessible, afin de faciliter sa compréhension, sa maintenance et son évolution.

**Important :** LegalAssist est un outil d'assistance à la recherche juridique. Il ne remplace pas les conseils d'un avocat, l'analyse d'un professionnel du droit ni la décision d'un juge. Les résultats doivent être vérifiés à partir des textes officiels et des sources juridiques applicables à Madagascar.

## Fonctionnalités disponibles

### 1. Gestion des utilisateurs

L'application comprend un système d'authentification avec stockage des mots de passe sous forme de hash BCrypt.

Cette fonctionnalité permet de contrôler l'accès à l'application et de préparer une gestion des rôles selon les responsabilités des utilisateurs.

### 2. Gestion des dossiers juridiques

Les utilisateurs peuvent organiser les dossiers au sein de l'application. Les fonctionnalités disponibles comprennent :

- L'ajout de dossiers.
- La recherche de dossiers.
- L'affichage des dossiers dans un tableau de bord.
- L'archivage des dossiers.

Cette organisation permet de retrouver plus facilement les informations et de conserver un historique de travail.

### 3. Assistant juridique local

LegalAssist intègre un système d'analyse de texte développé en Java.

À partir d'une description rédigée par l'utilisateur, le système peut :

- Identifier un domaine juridique probable, comme le droit foncier, le droit pénal ou le droit du travail.
- Rechercher dans la base de données les articles de loi associés aux mots-clés détectés.
- Produire un résumé simple de l'analyse.

Le système utilise actuellement une classification par mots-clés. Il s'agit d'une approche volontairement simple, dont les résultats dépendent du vocabulaire disponible et des données juridiques enregistrées dans la base.

### 4. Préparation des données d'entraînement

Le projet dispose d'un module permettant de charger un jeu de données CSV afin d'enrichir les informations utilisées par le système de classification.

Le fichier `training_data.csv` sert de support à cette préparation. Le fonctionnement exact dépend de la structure du jeu de données et de l'implémentation du module `TrainingModel`.

### 5. Génération de rapports PDF

Grâce à Apache PDFBox, l'application peut générer des rapports PDF à partir des informations traitées.

Cette fonctionnalité permet de conserver ou de partager une synthèse des informations relatives à un dossier.

### 6. Interface desktop

L'interface utilisateur est développée avec JavaFX et organisée autour de quatre écrans principaux :

- Connexion.
- Tableau de bord.
- Gestion des dossiers.
- Assistant juridique.

Les vues sont définies avec FXML, tandis que les contrôleurs gèrent les interactions et les traitements associés.

### 7. Base de données juridique

Un script SQL permet de créer les tables nécessaires au fonctionnement de l'application et d'insérer des exemples de lois et d'articles.

Les données enregistrées servent notamment à la recherche juridique et à l'identification des articles susceptibles de correspondre à une description.

## Technologies utilisées

| Technologie | Utilisation |
|---|---|
| Java | Langage principal de l'application |
| JavaFX | Création de l'interface desktop |
| FXML | Définition des écrans |
| PostgreSQL | Stockage des données |
| JDBC | Connexion à la base de données |
| Maven | Gestion des dépendances et exécution du projet |
| BCrypt | Hachage des mots de passe |
| Apache PDFBox | Génération de rapports PDF |
| CSV | Organisation des données utilisées pour la classification |

## Architecture du projet

Le code suit une architecture MVC, complétée par des services et des classes DAO pour séparer les responsabilités.

```text
LegalAssistAI/
├── pom.xml
├── sql/
│   └── schema.sql
├── src/
│   └── main/
│       ├── java/
│       │   └── com/legalassist/
│       │       ├── Main.java
│       │       ├── model/
│       │       ├── database/
│       │       ├── dao/
│       │       ├── ai/
│       │       ├── service/
│       │       ├── controller/
│       │       └── utils/
│       └── resources/
│           ├── fxml/
│           ├── css/
│           │   └── style.css
│           └── models/
│               └── training_data.csv
```

Les principaux dossiers ont les responsabilités suivantes :

- `model/` : représente les données manipulées par l'application.
- `database/` : gère la connexion à PostgreSQL.
- `dao/` : regroupe les classes chargées des opérations sur la base de données.
- `ai/` : contient le système de classification et les outils de préparation des données.
- `service/` : regroupe la logique métier et les traitements spécialisés.
- `controller/` : gère les interactions entre les écrans et les traitements.
- `utils/` : contient les fonctions utilitaires.
- `resources/fxml/` : regroupe les interfaces JavaFX.
- `resources/css/` : contient les styles de l'application.
- `resources/models/` : contient les données utilisées par le module d'analyse.

## Installation et configuration

### Prérequis

Pour exécuter le projet, vous aurez besoin de :

- Java JDK 17 ou une version compatible plus récente.
- PostgreSQL 13 ou une version compatible plus récente.
- Maven.
- IntelliJ IDEA ou NetBeans.

Une connexion Internet peut être nécessaire lors du premier téléchargement des dépendances Maven.

### 1. Cloner le dépôt

```bash
git clone URL_DU_DEPOT
cd LegalAssistAI
```

Remplacez `URL_DU_DEPOT` par l'adresse de votre dépôt GitHub.

### 2. Créer la base de données

Ouvrez PostgreSQL avec `psql` ou pgAdmin, puis exécutez :

```sql
CREATE DATABASE legalassist_db;
```

### 3. Configurer la connexion PostgreSQL

Ouvrez le fichier :

`src/main/java/com/legalassist/database/DatabaseConnection.java`

Vérifiez les paramètres de connexion et adaptez-les à votre environnement :

```java
private static final String URL =
    System.getenv().getOrDefault(
        "LEGALASSIST_DB_URL",
        "jdbc:postgresql://localhost:5432/legalassist_db"
    );

private static final String USER =
    System.getenv().getOrDefault(
        "LEGALASSIST_DB_USER",
        "postgres"
    );

private static final String PASSWORD =
    System.getenv().getOrDefault(
        "LEGALASSIST_DB_PASSWORD",
        ""
    );

private static final String USER = "postgres";
private static final String PASSWORD =
    System.getenv().getOrDefault("LEGALASSIST_DB_PASSWORD", "");

Remplacez les valeurs par celles de votre installation PostgreSQL. Pour une utilisation réelle, évitez de conserver les identifiants directement dans le code source.

### 4. Préparer le compte administrateur

Dans votre IDE, exécutez la classe :

`com.legalassist.utils.GenerateurMotDePasse`

Suivez les instructions du programme pour générer le hash BCrypt du mot de passe administrateur.

Conservez le mot de passe choisi et utilisez le hash généré à l'endroit prévu dans le script SQL.

### 5. Initialiser la base de données

Ouvrez le fichier :

`sql/schema.sql`

Remplacez le marqueur prévu pour le hash du mot de passe par le hash généré à l'étape précédente, puis exécutez le script dans la base `legalassist_db`.

Le script crée les tables nécessaires et ajoute les données juridiques d'exemple prévues par le projet.

### 6. Ouvrir le projet dans un IDE

Avec IntelliJ IDEA :

1. Ouvrez le menu `File > Open`.
2. Sélectionnez le dossier contenant le projet.
3. Laissez Maven charger les dépendances définies dans `pom.xml`.

Avec NetBeans, ouvrez le projet à partir du menu `File > Open Project`.

### 7. Lancer l'application

Depuis la racine du projet, exécutez :

```bash
mvn clean javafx:run
```

Vous pouvez également lancer `Main.java` directement depuis votre IDE si la configuration JavaFX est correctement prise en charge.

### 8. Se connecter

Utilisez les identifiants administrateur configurés lors de l'initialisation de la base de données.

Ne considérez pas un mot de passe par défaut comme `admin123` comme sûr pour un environnement réel. Modifiez-le et vérifiez le compte créé par le script SQL avant toute utilisation.

## Fonctionnement de l'analyse juridique

Le système de classification actuel repose sur une méthode par mots-clés, conçue pour être compréhensible et facile à faire évoluer.

Le traitement se déroule en plusieurs étapes :

1. Le texte saisi est nettoyé et découpé en mots.
2. Le système compare les mots obtenus aux mots-clés associés aux différents domaines juridiques.
3. Un score est calculé pour chaque domaine en fonction des correspondances détectées.
4. Le domaine ayant obtenu le meilleur score est proposé comme résultat.
5. Les mots-clés sont également utilisés pour rechercher les articles de loi correspondants dans la base de données.
6. Les données du fichier CSV peuvent être chargées afin d'enrichir les informations utilisées par le module.

Le fichier `ai/TextClassifier.java` contient la logique de classification, tandis que `ai/TrainingModel.java` gère le chargement des données d'entraînement.

Cette approche présente l'avantage d'être locale, explicable et relativement simple à comprendre. Cependant, un score de correspondance ne constitue pas une probabilité juridique fiable. Les résultats dépendent des mots-clés, de la qualité des données et de la couverture des textes enregistrés.

## Ajouter une nouvelle loi

En attendant la mise en place d'une interface dédiée, les lois et leurs articles peuvent être ajoutés directement dans PostgreSQL.

Exemple à adapter à la structure réelle de la base :

```sql
INSERT INTO loi (nom, domaine, date_publication, source)
VALUES (
    'Nom de la loi',
    'Droit civil',
    '2020-01-01',
    'Journal officiel'
);

INSERT INTO article_loi (loi_id, numero_article, contenu, mots_cles)
VALUES (
    1,
    'Art. 5',
    'Contenu de l''article...',
    'motcle1,motcle2,motcle3'
);
```

L'identifiant de la loi doit correspondre à celui réellement créé dans la base. Pour éviter les erreurs, vérifiez les clés étrangères et les contraintes définies dans `sql/schema.sql`.

Les mots-clés doivent être choisis avec soin, car ils servent à retrouver les articles lors de l'analyse des descriptions saisies par l'utilisateur.

Les exemples ci-dessus sont illustratifs et ne doivent pas être considérés comme des textes de loi authentiques.

## Évolutions envisagées

Plusieurs améliorations peuvent être apportées au projet :

- Ajouter une interface dédiée à la gestion des lois et des articles.
- Créer un écran pour gérer l'entraînement et le chargement des données.
- Développer un module de gestion des audiences.
- Renforcer la gestion des rôles et des autorisations.
- Faciliter l'importation de documents juridiques depuis des fichiers PDF ou DOC.
- Étudier l'intégration d'outils de traitement automatique du langage naturel, comme Weka ou Apache OpenNLP.
- Améliorer la qualité, la traçabilité et la vérification des sources juridiques.

Ces évolutions permettront d'améliorer progressivement les capacités de l'application sans abandonner son fonctionnement local.

## Limites et précautions

LegalAssist est un projet en évolution. Son système de classification repose actuellement sur des correspondances de mots-clés et ne comprend pas nécessairement toutes les nuances du langage juridique.

La pertinence des résultats dépend notamment de la qualité des lois et des articles présents dans la base de données.

Avant toute utilisation dans un dossier réel, les textes, les références et les résumés proposés doivent être vérifiés à partir de sources officielles. L'application ne garantit pas à elle seule l'exactitude ni l'exhaustivité d'une analyse juridique.

## Conclusion

LegalAssist Madagascar AI Offline explore la possibilité de rendre la recherche juridique plus accessible grâce à une application desktop fonctionnant localement.

Le projet associe développement Java, gestion de base de données et classification de texte afin de proposer un outil d'assistance à l'organisation des dossiers et à la recherche d'articles juridiques.

Il constitue également une base de travail pour approfondir les architectures logicielles, le traitement de texte et l'intégration progressive de fonctionnalités d'intelligence artificielle dans un contexte juridique malgache.

## Auteur et licence

**Auteur :** à compléter.

**Licence :** à définir.

La réutilisation et la redistribution du code seront soumises aux conditions de la licence choisie pour le dépôt.
