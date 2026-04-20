# Documentation Technique - SAE 6 Green IT

**Version** : 1.0  
**Dernière date de modification** : 20/02/2026  
**Auteurs** : DE-CLERCK Rafael, CHARPIOT Corentin, HENICHARD Théo, BOUJU Maxime, DELILLE Yannis

---

## Architecture générale

### Vue d'ensemble

L'application SAE 6 Green IT suit une architecture **Activity-based** typique d'Android.  
Elle est organisée en une série d'activités interconnectées permettant la navigation entre différents écrans. 

**Principes architecturaux** :
- **Activity-based UI** : Chaque écran est une Activity Android
- **Separation of concerns** : Les modèles métier (QuizItem, InfoItem) sont séparés de la présentation (Activities)
- **Intent-based navigation** : Communication entre activités via Intent
- **Pas de framework** : Pas d'injection de dépendances ni d'architecture MVVM/MVP pour le moment (évolution future)

### Diagramme de navigation

```
MainActivity
    ↓
HomeActivity (Écran d'accueil)
    ├── → DashboardActivity (Tableau de bord)
    ├── → QuizActivity (Quiz environnemental)
    ├── → InfoActivity (Fiches informatives)
    ├── → ProgressActivity (Progression utilisateur)
    └── → ProfileActivity (Profil utilisateur)
```

---

## Structure du code

### Organisation des fichiers

```
app/src/main/java/com/uphf/sae6_app/
├── MainActivity.java                # Point d'entrée principale
├── HomeActivity.java                # Écran d'accueil avec navigation
├── DashboardActivity.java           # Calcul impact CO2 e-mails
├── QuizActivity.java                # Quiz interactif avec filtrage
├── InfoActivity.java                # Fiches informatives paginées
├── ProgressActivity.java            # Progression utilisateur (placeholder)
├── ProfileActivity.java             # Profil utilisateur (placeholder)
├── QuizItem.java                    # Modèle de question
└── InfoItem.java                    # Modèle de fiche info

app/src/main/res/
├── layout/                          # Fichiers XML des layouts
│   ├── activity_main.xml            # Layout MainActivity
│   ├── activity_home.xml            # Layout HomeActivity (navigation)
│   ├── activity_dashboard.xml       # Layout DashboardActivity
│   ├── activity_quiz.xml            # Layout QuizActivity
│   ├── activity_info.xml            # Layout InfoActivity
│   ├── activity_progress.xml        # Layout ProgressActivity
│   ├── activity_profile.xml         # Layout ProfileActivity
│   └── item_card_home.xml           # Layout réutilisable pour cartes
├── drawable/                        # Images et ressources
├── values/                          # Strings, colors, themes
└── mipmap/                          # Icônes et launcher

app/
├── build.gradle                     # Configuration Gradle du module
├── proguard-rules.pro               # Règles de minification

build.gradle                         # Configuration Gradle du projet
gradle.properties                    # Propriétés du build
settings.gradle                      # Paramètres Gradle
```

---

## Composants et APIs

### 1. MainActivity

**Responsabilité** : Point d'entrée de l'application, active au démarrage de l'application.

**Appels suivants** : Redirection implicite vers `HomeActivity` via intent filter dans AndroidManifest.xml

---

### 2. HomeActivity

**Responsabilité** : Menu principal de navigation avec profil utilisateur et cartes d'accès aux fonctionnalités.

**Cas d'utilisation** :
- Affichage du profil utilisateur actuel
- Navigation rapide vers toutes les fonctionnalités principales

---

### 3. DashboardActivity

**Responsabilité** : Calcul et visualisation de l'impact carbone lié au stockage d'e-mails.

**Algorithme de calcul** :

```
Impact CO2e/an = (stock moyen d'e-mails) × (CO2 par mail/an) × 365 jours

Paramètres :
- mailsPerDay = 20 (mails reçus/jour)
- co2PerMailPerYear = 0.0004 kg CO2e/mail/an
- daysBetweenClean = 1 à 30 (fréquence de nettoyage)
- unsubscribed = booléen (désabonnement -30% mails)

Calcul du stock moyen :
- stock moyen = (mailsPerDay × daysBetweenClean) / 2
- Si désabonné : mailsPerDay *= 0.7 (réduction de 30%)
```

**Exemple numérique** :
```
Scénario par défaut (7 jours, non désabonné) :
- stock moyen = (20 × 7) / 2 = 70 mails
- impact annuel = 70 × 0.0004 × 365 = 10.22 kg CO2e/an
- progression = (10.22 / 50) × 100 = 20.4% ≈ 20
```

**Limitations actuelles** :
- Valeurs CO2 fictives (pour démonstration)
- Pas de persistance des données utilisateur
- Pas d'historique ou comparaison
- Manque d'aspect graphique

---

### 4. QuizActivity

**Responsabilité** : Présentation et gestion d'un quiz interactif avec filtrage par thème et difficulté.


**Données de démonstration** (4 questions chargées) :
```
Q1: Gaz effet de serre → Thème: climat, Difficulté: 1
Q2: Efficacité énergétique → Thème: energie, Difficulté: 1
Q3: Recyclage verre → Thème: dechets, Difficulté: 2
Q4: Décomposition plastique → Thème: dechets, Difficulté: 3
```

**Logique de réponse** :
1. Utilisateur clique sur une réponse
2. `onAnswerSelected(int index)` exécutée :
   - Désactiver tous les boutons
   - Si correcte → bouton vert (#C8E6C9) + feedback positif
   - Si incorrecte → bouton rouge (#FFCDD2) + affichage réponse correcte
3. Affichage du bouton "Suivant"

**Gestion d'erreur** :
- Si aucune question après filtrage → message "Aucune question disponible"
- Bouton "Suivant" devient "Retour"

---

### 5. InfoActivity

**Responsabilité** : Affichage séquentiel de fiches informatives écologiques.

**Fiches de démonstration chargées** :
```
1. "Réduire sa consommation électrique"
2. "Recycler le verre"
3. "Nettoyage des e-mails"
4. "Favoriser les transports actifs"
```

**Gestion des images** :
- Tentative de charger ressource drawable par ID
- Si ressource inexistante → ImageView invisible
- Placeholder supporté pour développement

---

### 6. ProgressActivity & ProfileActivity

**Status** : Placeholders (IHM minimaliste, prêtes pour évolution)

**Prévues pour** :
- **ProgressActivity** : Visualisation progression utilisateur (graphiques, statistiques)
- **ProfileActivity** : Gestion profil, paramètres, sauvegarde données

---

## Flux de données

### Flux général d'une session utilisateur

```
[Démarrage App]
    ↓
[MainActivity - initialisation edge-to-edge]
    ↓
[HomeActivity - affichage profil + cartes nav]
    ↓
[Utilisateur clique sur une carte]
    ├─→ [DashboardActivity]
    │   ├─ Données fictives (paramètres CO2)
    │   └─ Calculs temps réel via listeners
    │
    ├─→ [QuizActivity]
    │   ├─ Chargement questions depuis List<QuizItem>
    │   ├─ Application filtres (optionnel)
    │   └─ Navigation question par question
    │
    ├─→ [InfoActivity]
    │   ├─ Chargement fiches depuis List<InfoItem>
    │   └─ Pagination séquentielle
    │
    ├─→ [ProgressActivity] (placeholder)
    │
    └─→ [ProfileActivity] (placeholder)
```

### Absence de persistance

**Limitation actuelle** :
- Aucune base de données (Room, SQLite)
- Aucun SharedPreferences
- Données réinitialisées à chaque lancement app

**Implications** :
- Score utilisateur non sauvegardé
- Réponses au quiz non trackées
- Progression perdue au redémarrage

---

## Cas d'utilisation détaillés

### Cas 1 : Estimer son impact CO2 lié aux e-mails

**Acteur** : Utilisateur sensibilisé aux enjeux écologiques

**Flux** :
1. Cliquer sur carte "Tableau de Bord" (HomeActivity)
2. DashboardActivity s'ouvre
3. Utilisateur ajuste le curseur fréquence nettoyage (ex: 14 jours)
4. Valeur affichée : "15.84 kg CO2e / an"
5. (Optionnel) Cocher "Désabonnement" → impact réduit à ~11.09 kg
6. Revenir (back button) → HomeActivity

**Données utilisées** :
- mailsPerDay = 20 (constant)
- co2PerMailPerYear = 0.0004 (constant)
- daysBetweenClean = slider 1-30
- unsubscribed = switch

**Calcul exemple** :
```
daysBetweenClean = 14, unsubscribed = false
→ stock moyen = 140 mails
→ impact = 140 × 0.0004 × 365 = 20.44 kg CO2e/an
```

---

### Cas 2 : Répondre à un quiz sur climat

**Acteur** : Utilisateur apprenant

**Flux** :
1. HomeActivity → cliquer "Quiz"
2. QuizActivity charge 4 questions
3. Affichage Q1: "Quel est le principal gaz..."
4. Utilisateur clique sur réponse 2 (correcte)
5. Feedback vert : "Bonne réponse ! Le CO2..."
6. Bouton "Suivant" → Q2
7. (Optionnel) "Précédent" pour réviser Q1
8. Après Q4 → "Terminer" close activity

**Données chargées** :
- `allItems` contient 4 QuizItem
- Filtre appliqué : theme=null, difficulty=-1 (aucun filtre)
- `items` = `allItems` (4 questions affichées)

**Interactivité** :
- Boutons réponse changent de couleur selon résultat
- `quiz_info` affiche feedback immédiat
- Navigation prev/next disponible

---

### Cas 3 : Consulter des fiches écologiques

**Acteur** : Utilisateur cherchant conseils pratiques

**Flux** :
1. HomeActivity → cliquer "Fiches Info"
2. InfoActivity charge 4 fiches
3. Affichage fiche 1 : "Réduire sa consommation électrique"
4. Lire contenu + consulter image (si présente)
5. Compteur affiche "1 / 4"
6. Cliquer "Suivant" → fiche 2
7. Après fiche 4 → bouton "Terminé" (close activity)

**Pagination** :
```
Page 1: "Réduire sa consommation électrique" (1/4)
Page 2: "Recycler le verre" (2/4)
Page 3: "Nettoyage des e-mails" (3/4)
Page 4: "Favoriser les transports actifs" (4/4)
```

---

## Modèles de données

### QuizItem

```java
public class QuizItem {
    // Obligatoires
    public int id;                    // Identificateur unique (ex: 1)
    public String question;           // Énoncé (ex: "Quel gaz...")
    public List<String> answers;      // Liste de 4 réponses
    public int correctIndex;          // Index correct (0-3)
    
    // Optionnels
    public String infos;              // Explications (ex: "Le CO2 est...")
    public String theme;              // Catégorie (ex: "climat")
    public int difficulty;            // 1=facile, 2=moyen, 3=difficile
    public String image;              // Drawable (ex: "ic_quiz_climate")
}
```

**Méthodes utiles** :
```java
public String getTheme() { return theme; }
public int getDifficulty() { return difficulty; }
public String getImage() { return image; }
```

---

### InfoItem

```java
public class InfoItem {
    public String title;      // Titre (ex: "Réduire sa consommation...")
    public String content;    // Description détaillée
    public String imageName;  // Drawable optionnel
}
```

---

### Retour à l'activity précédente

```java
// Tous les types d'activity
finish(); // Ferme l'activity, retour à HomeActivity

// Back button système gère automatiquement
// (Android app stack)
```

---

## Dépendances externes

### Gradle - Module app

**Fichier** : `app/build.gradle`

### libs.versions.toml - Version centralisée

**Fichier** : `gradle/libs.versions.toml`

Définit versions des libs (référencées par `libs.*` dans build.gradle)

### Robolectric

**Rôle** : Framework de test Android sans émulateur
- Exécute tests unitaires localement (JVM)
- Simule contexte Android

**Limitations** :
- Requiert AGP (Android Gradle Plugin) compatible ≤ 8.13.0
- Version actuelle : 4.11.1

---

## Évolutions futures et points d'extension

### 1. Persistance de données

**Objectif** : Sauvegarder progression utilisateur

**Possibles points d'extension** :
- `HomeActivity.onCreate()` → charger données utilisateur depuis DB
- `QuizActivity.onAnswerSelected()` → logger réponses en DB
- `DashboardActivity.recalcImpact()` → sauvegarder calculs

### 2. Architecture MVVM/MVP

**Actuellement** : Activities contiennent logique métier

**Bénéfices** :
- Testabilité améliorée
- Séparation concerns
- Réutilisabilité code

### 3. Système de points

**Logique** :
- Débloquer achievement après actions (quiz réussi, 10 fiches lues)
- Calculer total points
- Afficher dans ProfileActivity

### 4. Graphiques et visualisations

**Bibliothèque** : MPAndroidChart ?
```gradle
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
```

**Cas d'usage** :
- Historique impact CO2 (graph ligne)
- Répartition par thème quiz (pie chart)
- Progression utilisateur (bar chart)

### 5. Support multilingue (priorité faible)

**Actuellement** : Français uniquement (`strings.xml`)

**Évolution** :
```
res/values/strings.xml          → Français
res/values-en/strings.xml       → Anglais
```