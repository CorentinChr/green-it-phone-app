# Documentation Technique - SAE 6 Green IT

**Version** : 1.1  
**Dernière date de modification** : 21/04/2026  
**Auteurs** : DE-CLERCK Rafael, CHARPIOT Corentin, HENICHARD Théo, BOUJU Maxime, DELILLE Yannis

---

## Architecture générale

### Vue d'ensemble

L'application SAE 6 Green IT suit une architecture **Activity-based** Android avec :
- une couche UI basée sur des `Activity`
- des modèles simples (`model/*`) sérialisés par Gson
- un accès réseau via Retrofit (`retrofit/*`)

**Principes architecturaux** :
- **Activity-based UI** : chaque écran principal est une Activity
- **Intent-based navigation** : passage de paramètres (`theme`, `difficulty`) via Intent
- **State in SharedPreferences** : niveau, score, identifiant utilisateur et historiques
- **API-first pour les contenus** : quiz, fiches et data visualisation chargés depuis backend

### Diagramme de navigation

```
HomeActivity (launcher)
    ├── → QuizLevelActivity (quiz de positionnement)
    │      └── → QuizLevelResultActivity
    ├── → DashboardActivity
    ├── → QuizActivity
    ├── → InfoActivity
    ├── → DataVizActivity
    ├── → ProgressActivity
    └── → ProfileActivity

MainActivity (présente dans le projet, non launcher)
```

---

## Structure du code

### Organisation des fichiers

```
app/src/main/java/com/uphf/sae6_app/
├── MainActivity.java                 # Activity edge-to-edge générique
├── HomeActivity.java                 # Écran d'accueil + logique de déverrouillage
├── DashboardActivity.java            # Estimation impact CO2e + pie chart
├── QuizActivity.java                 # Quiz thématique (API + filtres)
├── QuizLevelActivity.java            # Quiz de positionnement /10
├── QuizLevelResultActivity.java      # Résultat du quiz de positionnement
├── InfoActivity.java                 # Fiches interactives (étapes + mini-quiz)
├── DataVizActivity.java              # Visualisation Green IT via RecyclerView
├── DataVizAdapter.java               # Affichage conditionnel selon niveau utilisateur
├── ProgressActivity.java             # Suivi moyennes + historique des scores
├── ProfileActivity.java              # Nom + niveau utilisateur
├── ScoreStorage.java                 # Utilitaire historique scores (JSON en prefs)
├── model/
│   ├── QuizItem.java                 # Modèle question quiz
│   ├── InfoItem.java                 # Modèle fiche + étapes interactives
│   └── GreenItData.java              # Modèle data fabrication/usage
└── retrofit/
    ├── RetrofitClient.java           # Singleton Retrofit + OkHttp
    ├── GreenItApi.java               # Endpoints quiz/fiches/data
    ├── ApiService.java               # Endpoints utilisateurs (POST/GET)
    └── dto/
        ├── UserRequest.java
        └── UserResponse.java

app/src/test/java/com/uphf/sae6_app/
├── DashboardActivityTest.java
├── DataVizActivityTest.java
├── DataVizAdapterTest.java
├── HomeActivityTest.java
├── InfoActivityTest.java
├── MainActivityTest.java
├── ProfileActivityTest.java
├── ProgressActivityTest.java
├── QuizActivityTest.java
├── QuizLevelActivityTest.java
├── QuizLevelResultActivityTest.java
├── ScoreStorageTest.java
├── QuizItemTest.java                 # Fichier présent (vide actuellement)
└── InfoItemTest.java                 # Fichier présent (vide actuellement)
```

---

## Composants et APIs

### 1. HomeActivity

**Responsabilité** : écran d'accueil, affichage profil, navigation vers tous les modules.

**Points clés** :
- lit `SharedPreferences` (`prefs_user`) pour afficher nom, niveau, score
- masque/affiche la grille principale selon `KEY_LEVEL_DONE`
- propose le bouton `btn_quiz_test` pour lancer `QuizLevelActivity`
- ouvre un sélecteur de thème avant `QuizActivity` et `InfoActivity`

---

### 2. QuizLevelActivity

**Responsabilité** : quiz de placement (10 questions), calcul du niveau utilisateur, persistance.

**Clés de persistance** :
- `PREFS_NAME = "prefs_user"`
- `KEY_LEVEL_DONE`
- `KEY_USER_LEVEL` (`beginner` / `intermediate` / `advanced`)
- `KEY_USER_SCORE_10`
- `KEY_USER_ID`, `KEY_USER_NAME`

**Logique de niveau** :
- score `0..3` -> `beginner`
- score `4..7` -> `intermediate`
- score `8..10` -> `advanced`

**Effets de fin de quiz** :
- sauvegarde prefs
- historisation via `ScoreStorage.addScore(..., KEY_HISTORY_LEVEL, ...)`
- upload backend (`ApiService.upsertUser`)
- redirection vers `QuizLevelResultActivity`

---

### 3. QuizLevelResultActivity

**Responsabilité** : afficher score `/10` + niveau lisible (`Débutant`, `Intermédiaire`, `Difficile`).

**Comportement** :
- fallback utilisateur si aucun résultat stocké
- bouton retour vers l'écran précédent

---

### 4. DashboardActivity

**Responsabilité** : estimer un impact CO2e annuel global et afficher sa répartition.

**Catégories calculées** :
- **Mails** : impact lié au stockage + fréquence de nettoyage + désabonnement
- **Matériel** : nombre d'appareils/an × coût CO2 fictif
- **Services** : heures de streaming/semaine × qualité vidéo (SD/HD/UHD)

**UI associée** :
- `SeekBar` (nettoyage, matériel, services)
- `SwitchCompat` (désabonnement)
- `Spinner` (qualité vidéo)
- `ProgressBar` (normalisation 0..100)
- `PieChart` MPAndroidChart (répartition par catégorie)

---

### 5. QuizActivity

**Responsabilité** : quiz thématique principal, alimenté par API.

**Comportement** :
- récupère `theme` et `difficulty` depuis Intent
- si difficulté absente : déduit depuis niveau utilisateur (`prefs_user`)
- appelle `GreenItApi.getQuiz()`
- filtre côté client : thème + difficulté (`q.difficulty <= userDifficulty`)
- feedback immédiat sur réponses, navigation précédent/suivant
- en fin : score en `%` historisé dans `ScoreStorage.KEY_HISTORY_QUIZ`

---

### 6. InfoActivity

**Responsabilité** : affichage de fiches pédagogiques, potentiellement multi-étapes.

**Fonctionnalités** :
- chargement API via `GreenItApi.getInfos()`
- filtrage par thème + difficulté utilisateur
- support d'étapes texte/image
- support d'étapes quiz intégrées (`InfoItem.InfoStep.Quiz`)
- gestion d'un format API "plat" avec reconstruction quiz via `ensureQuiz()`

---

### 7. DataVizActivity + DataVizAdapter

**Responsabilité** : visualisation des données Green IT (fabrication/usage) selon niveau utilisateur.

**DataVizActivity** :
- charge le niveau utilisateur depuis `prefs_user`
- initialise `RecyclerView`
- lit la catégorie via `spinner_category`
- récupère les données API via `GreenItApi.getGreenItData()`

**DataVizAdapter** :
- `beginner` : affichage synthétique
- `intermediate` : affichage intermédiaire
- `advanced` : affichage complet + source
- bascule `fabrication` / `usage` en masquant/affichant les champs pertinents

---

### 8. ProgressActivity

**Responsabilité** : suivi de progression basé sur les historiques de scores.

**Règles** :
- moyenne quiz principal (`%`) >= 70
- moyenne quiz de niveau (`/10`) >= 7
- si les deux seuils sont atteints : message "Prêt à passer au niveau supérieur"

---

### 9. ProfileActivity

**Responsabilité** : gestion du nom et du niveau utilisateur.

**Comportement** :
- lit/écrit `user_name` + `user_level` dans `prefs_user`
- conversion libellés UI (`Débutant`, `Intermédiaire`, `Avancé`) -> clés internes
- persistance au clic sur le bouton d'enregistrement

---

### 10. APIs Retrofit

#### GreenItApi

- `GET /greenitdata` -> `List<GreenItData>`
- `GET /fiches-complexes` -> `List<InfoItem>`
- `GET /quiz` -> `List<QuizItem>`

#### ApiService

- `POST /users` -> upsert profil utilisateur (`UserRequest`)
- `GET /users/{id}` -> lecture utilisateur

#### RetrofitClient

- Base URL : `https://backendsae-production.up.railway.app/`
- Logging : `HttpLoggingInterceptor.Level.BASIC`
- Timeouts : connect 15s / read 20s

---

## Flux de données

### Flux principal d'une session

```
[Démarrage]
    ↓
[HomeActivity]
    ↓
[Si quiz de niveau non fait]
    → QuizLevelActivity → QuizLevelResultActivity → HomeActivity
    ↓
[Cartes déverrouillées]
    ├─→ DashboardActivity (calcul local)
    ├─→ QuizActivity (API + filtres + score local)
    ├─→ InfoActivity (API + étapes interactives)
    ├─→ DataVizActivity (API + adaptation par niveau)
    ├─→ ProgressActivity (lecture historiques locaux)
    └─→ ProfileActivity (édition profil local)
```

### Persistance locale

**Mécanisme** : `SharedPreferences` + `ScoreStorage`

**Données persistées** :
- statut quiz de niveau
- niveau utilisateur
- score quiz de niveau
- identifiant utilisateur UUID
- nom utilisateur
- historiques quiz (`history_quiz`, `history_level`) en JSON array

---

## Cas d'utilisation détaillés

### Cas 1 : Débloquer les fonctionnalités via quiz de positionnement

**Acteur** : nouvel utilisateur

**Flux** :
1. Ouvrir l'application (`HomeActivity`)
2. Les cartes principales sont masquées
3. Cliquer sur "Quiz test"
4. Répondre aux 10 questions
5. Voir le résultat (`QuizLevelResultActivity`)
6. Retour à l'accueil, cartes désormais disponibles

**Données écrites** :
- `level_done=true`
- `user_level`
- `user_score_10`
- `user_id` (créé si absent)
- historique `history_level`

---

### Cas 2 : Estimer l'impact CO2e numérique

**Acteur** : utilisateur souhaitant évaluer son impact

**Flux** :
1. Home -> Dashboard
2. Régler fréquence de nettoyage mails
3. Indiquer appareils/an
4. Régler heures de streaming et qualité vidéo
5. Lire impact total + répartition par catégorie

**Sorties** :
- texte "X.XX kg CO2e / an"
- barre de progression (0..100)
- pie chart de répartition

---

### Cas 3 : Répondre à un quiz thématique adapté au niveau

**Acteur** : utilisateur ayant un niveau enregistré

**Flux** :
1. Home -> choisir un thème
2. `QuizActivity` charge les questions API
3. Filtrage par thème + difficulté <= niveau utilisateur
4. Réponses avec feedback immédiat
5. Fin du quiz -> score `%` enregistré dans historique

---

### Cas 4 : Consulter des fiches interactives

**Acteur** : utilisateur en apprentissage progressif

**Flux** :
1. Home -> choisir un thème
2. `InfoActivity` charge les fiches API
3. Filtrage par thème + difficulté
4. Parcours des étapes (texte/image/quiz)
5. Validation des mini-quiz pour continuer

---

### Cas 5 : Visualiser les données Green IT

**Acteur** : utilisateur voulant comparer fabrication/usage

**Flux** :
1. Home -> DataViz
2. Chargement API `greenitdata`
3. Sélection catégorie (`fabrication` ou `usage`)
4. Affichage adapté au niveau (`beginner/intermediate/advanced`)

---

## Modèles de données

### QuizItem

```java
public class QuizItem {
    public int id;
    public String question;
    public List<String> answers;
    public int correctIndex;
    public String infos;
    public String theme;
    public int difficulty;   // 1=facile, 2=moyen, 3=difficile
    public String image;     // drawable optionnel
}
```

---

### InfoItem

```java
public class InfoItem {
    public String title;
    public String content;
    public String imageName;
    public int difficulty;
    public String theme;
    public List<InfoStep> steps;

    public static class InfoStep {
        public String text;
        public String imageName;
        public Quiz quiz;
        public Integer id;
        public String type;              // TEXT ou QUIZ (API)
        public String question;
        public List<String> answers;
        public Integer correctIndex;

        public void ensureQuiz() { ... }
    }

    public static class Quiz {
        public String question;
        public List<String> answers;
        public int correctIndex;
    }
}
```

---

### GreenItData

```java
public class GreenItData {
    public String device;
    public double co2ManufacturingKg;
    public double energyManufacturingKwh;
    public double energyUseKwhPerYear;
    public double co2UseKgPerYear;
    public String source;
}
```

---

### ScoreStorage

```java
public final class ScoreStorage {
    public static final String KEY_HISTORY_QUIZ = "history_quiz";
    public static final String KEY_HISTORY_LEVEL = "history_level";

    public static void addScore(Context context, String historyKey, int score, int maxItems) { ... }
    public static List<Integer> getScores(Context context, String historyKey) { ... }
    public static double average(List<Integer> scores) { ... }
    public static String formatList(List<Integer> scores, String suffix) { ... }
}
```

---

## Dépendances externes

### Gradle - Module app

**Fichier** : `app/build.gradle`

Dépendances notables :
- `com.github.PhilJay:MPAndroidChart:v3.1.0`
- `com.squareup.retrofit2:retrofit:2.9.0`
- `com.squareup.retrofit2:converter-gson:2.9.0`
- `com.squareup.okhttp3:logging-interceptor:4.9.3`
- `org.robolectric:robolectric:4.16.1` (tests)

### Version catalog

**Fichier** : `gradle/libs.versions.toml`

- AGP défini actuellement à `9.0.1`
- JUnit4, AndroidX test, AppCompat, Material, Activity, ConstraintLayout

### Robolectric

**Rôle** : tests unitaires Android en JVM sans émulateur

**Config projet** :
- Dépendance : `org.robolectric:robolectric:4.16.1`
- Fichier : `app/src/test/resources/robolectric.properties`
- SDK Robolectric : `sdk=34`

**Attention compatibilité** :
- La compatibilité IDE/AGP peut bloquer la résolution en test si la version AGP du projet dépasse la version supportée par Android Studio installé.

---

## Évolutions futures et points d'extension

### 1. Gestion des erreurs réseau et mode dégradé

**Actuellement** : en cas d'échec API, certaines Activities ferment immédiatement.

**Évolution** :
- fallback local (`loadSampleData`, `loadSampleInfos`) activable
- cache offline des dernières réponses API
- stratégie de retry/backoff

### 2. Refactoring architecture (MVVM + Repository)

**Actuellement** : logique métier majoritairement dans Activities.

**Évolution** :
- ViewModel par écran
- Repository pour accès Retrofit/stockage
- meilleure testabilité unitaire

### 3. Harmonisation des clés et constantes

**Actuellement** : certaines clés prefs sont dupliquées en littéraux dans plusieurs classes.

**Évolution** :
- centraliser les clés dans une classe dédiée
- réduire les risques d'incohérence inter-écrans

### 4. Couverture de tests

**Actuellement** : base de tests Robolectric fonctionnelle mais incomplète.

**Évolution** :
- compléter `QuizItemTest` et `InfoItemTest`
- ajouter tests d'intégration UI (Espresso)
- mocker proprement la couche Retrofit dans les tests

### 5. Internationalisation et accessibilité

**Actuellement** : contenu majoritairement français.

**Évolution** :
- `res/values-en/strings.xml`
- amélioration des content descriptions et contrastes
- adaptation textes dynamiques pour accessibilité

