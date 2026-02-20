# Documentation Technique - SAE 6 Green IT

**Version** : 1.0  
**Date** : Février 2026  
**Auteurs** : DE-CLERCK Rafael, CHARPIOT Corentin, HENICHARD Théo, BOUJU Maxime, DELILLE Yannis

---

## Table des matières

1. [Architecture générale](#architecture-générale)
2. [Structure du code](#structure-du-code)
3. [Composants et APIs](#composants-et-apis)
4. [Flux de données](#flux-de-données)
5. [Cas d'utilisation détaillés](#cas-dutilisation-détaillés)
6. [Modèles de données](#modèles-de-données)
7. [Navigation et intents](#navigation-et-intents)
8. [Dépendances externes](#dépendances-externes)
9. [Configuration de build](#configuration-de-build)
10. [Évolutions futures et points d'extension](#évolutions-futures-et-points-dextension)

---

## Architecture générale

### Vue d'ensemble

L'application SAE 6 Green IT suit une architecture **Activity-based** typique d'Android. Elle est organisée en une série d'activités interconnectées permettant la navigation entre différents écrans. 

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
    ├── → DashboardActivity (Tableau de bord CO2)
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

**Responsabilité** : Point d'entrée de l'application, actif uniquement lors du premier lancement.

**Code** :
```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Applique edge-to-edge layout
    }
}
```

**Fonctionnalités** :
- Initialise l'interface edge-to-edge (Android 5.0+)
- Prépare les insets système (barres de navigation et d'état)

**Appels suivants** : Redirection implicite vers `HomeActivity` via intent filter dans AndroidManifest.xml

---

### 2. HomeActivity

**Responsabilité** : Écran principal de navigation avec profil utilisateur et cartes d'accès aux fonctionnalités.

**Code structuré** :
```java
public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Chargement des données utilisateur fictives
        // 2. Récupération des vues de cartes
        // 3. Binding des listeners de navigation
        // 4. Mise à jour des titres des cartes
    }
}
```

**Composants UI** :
| Composant | Fonction |
|-----------|----------|
| `user_name` (TextView) | Affiche le nom utilisateur |
| `user_score` (TextView) | Affiche le score utilisateur |
| `avatar` (ImageView) | Photo de profil utilisateur |
| `card_dashboard` | Navigation vers Dashboard |
| `card_quiz` | Navigation vers Quiz |
| `card_info` | Navigation vers Fiches Info |
| `card_progress` | Navigation vers Progression |
| `card_profile` | Navigation vers Profil |

**Données** : Actuellement fictives, stockées dans `strings.xml` :
- `user_name_default` : nom de l'utilisateur
- `user_score_default` : score initial
- Titres des cartes (`card_dashboard`, `card_quiz`, etc.)

**Intents générés** :
```java
// Exemple pour Dashboard
startActivity(new Intent(this, DashboardActivity.class));
```

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

**Composants UI** :
| ID | Type | Fonction |
|----|------|----------|
| `seekbar_clean_interval` | SeekBar | Sélection fréquence (1-30 jours) |
| `txt_interval_value` | TextView | Affichage valeur sélectionnée |
| `switch_unsubscribe` | SwitchCompat | Activation désabonnement |
| `txt_impact_value` | TextView | Affichage CO2 estimé |
| `progressImpact` | ProgressBar | Visualisation graphique |

**Listeners implémentés** :
1. **SeekBar.OnSeekBarChangeListener** :
   - `onProgressChanged()` : Recalcule l'impact à chaque changement
   - Met à jour l'affichage en temps réel

2. **CompoundButton.OnCheckedChangeListener** :
   - Recalcule l'impact si désabonnement coché/décoché

**Flux d'interaction** :
1. Utilisateur déplace le curseur (1-30 jours)
2. `onProgressChanged()` appelé
3. `recalcImpact()` exécutée
4. UI mise à jour avec la valeur en kg CO2e/an

**Limitations actuelles** :
- Valeurs CO2 fictives (pour démonstration)
- Pas de persistance des données utilisateur
- Pas d'historique ou comparaison

---

### 4. QuizActivity

**Responsabilité** : Présentation et gestion d'un quiz interactif avec filtrage par thème et difficulté.

**Modèle de données** : `List<QuizItem>`

```java
public class QuizItem {
    int id;                    // Identifiant unique
    String question;           // Texte de la question
    List<String> answers;      // 4 réponses proposées
    int correctIndex;          // Index (0-3) de la bonne réponse
    String infos;              // Explication/feedback
    String theme;              // Thème : "climat", "energie", "dechets"
    int difficulty;            // Niveau : 1=facile, 2=moyen, 3=difficile
    String image;              // Nom ressource drawable (optional)
}
```

**Données de démonstration** (4 questions chargées) :
```
Q1: Gaz effet de serre → Thème: climat, Difficulté: 1
Q2: Efficacité énergétique → Thème: energie, Difficulté: 1
Q3: Recyclage verre → Thème: dechets, Difficulté: 2
Q4: Décomposition plastique → Thème: dechets, Difficulté: 3
```

**Système de filtrage** :
```java
private void applyFilters(String theme, int difficulty) {
    // Filtre par thème ET difficulté (AND logique)
    // theme = null → pas de filtre thème
    // difficulty ≤ 0 → pas de filtre difficulté
}
```

**Paramètres Intent optionnels** :
```java
Intent intent = getIntent();
String themeFilter = intent.getStringExtra("theme");     // ex: "climat"
int difficultyFilter = intent.getIntExtra("difficulty", -1); // ex: 2
```

**Composants UI** :
| ID | Type | Rôle |
|----|------|------|
| `quiz_image` | ImageView | Illustration question (optionnelle) |
| `quiz_question` | TextView | Énoncé de la question |
| `answer_0` à `answer_3` | Button[] | Boutons réponses |
| `quiz_info` | TextView | Feedback après réponse |
| `quiz_info` | TextView | Compteur position |
| `prev_btn` | Button | Navigation précédente |
| `next_btn` | Button | Navigation suivante |

**Logique de réponse** :
1. Utilisateur clique sur une réponse
2. `onAnswerSelected(int index)` exécutée :
   - Désactiver tous les boutons
   - Si correcte → bouton vert (#C8E6C9) + feedback positif
   - Si incorrecte → bouton rouge (#FFCDD2) + affichage réponse correcte
3. Affichage du bouton "Suivant"

**Navigation** :
```java
showNext() {
    if (currentIndex < items.size() - 1) {
        // Aller à la question suivante
    } else {
        // Terminer le quiz
        finish();
    }
}

showPrevious() {
    if (currentIndex > 0) {
        // Aller à la question précédente
    }
}
```

**Gestion d'erreur** :
- Si aucune question après filtrage → message "Aucune question disponible"
- Bouton "Suivant" devient "Retour"

---

### 5. InfoActivity

**Responsabilité** : Affichage séquentiel de fiches informatives écologiques.

**Modèle de données** : `List<InfoItem>`

```java
public class InfoItem {
    String title;      // Titre de la fiche
    String content;    // Contenu/description
    String imageName;  // Nom ressource drawable (optional)
}
```

**Fiches de démonstration chargées** :
```
1. "Réduire sa consommation électrique"
2. "Recycler le verre"
3. "Nettoyage des e-mails"
4. "Favoriser les transports actifs"
```

**Composants UI** :
| ID | Type | Fonction |
|----|------|----------|
| `info_image` | ImageView | Illustration fiche |
| `info_title` | TextView | Titre fiche |
| `info_content` | TextView | Contenu/description |
| `info_counter` | TextView | Position actuelle (ex: "2 / 4") |
| `btn_next_info` | Button | Suivant/Terminé |

**Logique de pagination** :
```java
private void displayCurrent() {
    InfoItem it = items.get(currentIndex);
    // Affiche titre + contenu
    // Charge image si exists
    // Affiche compteur "X / Y"
    
    // Ajuste texte bouton
    if (currentIndex < items.size() - 1) {
        btnNext.setText("Suivant");
    } else {
        btnNext.setText("Terminé");
    }
}

private void onNext() {
    if (currentIndex < items.size() - 1) {
        currentIndex++;
        displayCurrent();
    } else {
        finish(); // Retour à HomeActivity
    }
}
```

**Gestion des images** :
- Tentative de charger ressource drawable par ID
- Si ressource inexistante → ImageView invisible
- Placeholder supporté pour développement

---

### 6. ProgressActivity & ProfileActivity

**Status** : Placeholders (IHM minimaliste, prêtes pour évolution)

```java
// Deux activités identiques dans structure
public class ProgressActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        // À compléter avec logique de progression
    }
}
```

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

**Exemple d'instance** :
```java
new QuizItem(
    1,                                         // id
    "Quel est le principal gaz...",           // question
    Arrays.asList("Oxygène", "Dioxyde de carbone", "Azote", "Hélium"),
    1,                                         // correctIndex (réponse 2)
    "Bonne réponse ! Le CO2 est l'un des...", // infos
    "climat",                                  // theme
    1,                                         // difficulty (facile)
    "placeholder"                              // image
)
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

**Exemple d'instance** :
```java
new InfoItem(
    "Réduire sa consommation électrique",
    "Éteignez les appareils en veille et privilégiez des appareils économes...",
    "placeholder"
)
```

---

## Navigation et intents

### Intent filter - Point d'entrée

**AndroidManifest.xml** :
```xml
<activity android:name=".HomeActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

**Effet** : Au lancement app, Android lance HomeActivity directement (pas MainActivity)

### Intents explicites

**HomeActivity → autres activities** :
```java
// Navigation simple
startActivity(new Intent(this, DashboardActivity.class));
startActivity(new Intent(this, QuizActivity.class));

// Navigation avec paramètres (quiz)
Intent intent = new Intent(this, QuizActivity.class);
intent.putExtra("theme", "climat");
intent.putExtra("difficulty", 2);
startActivity(intent);
```

### Récupération des paramètres

```java
// Dans QuizActivity.onCreate()
Intent intent = getIntent();
String themeFilter = null;
int difficultyFilter = -1;

if (intent != null) {
    if (intent.hasExtra("theme")) {
        themeFilter = intent.getStringExtra("theme");
    }
    if (intent.hasExtra("difficulty")) {
        difficultyFilter = intent.getIntExtra("difficulty", -1);
    }
}

applyFilters(themeFilter, difficultyFilter);
```

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

```groovy
dependencies {
    // AndroidX - Framework Android moderne
    implementation libs.appcompat      // AppCompatActivity, compat APIs
    implementation libs.material       // Material Design components
    implementation libs.activity       // Activity-ktx, edge-to-edge support
    implementation libs.constraintlayout // ConstraintLayout (layouts XML)
    
    // Tests unitaires
    testImplementation libs.junit      // JUnit 4 (test framework)
    testImplementation 'org.robolectric:robolectric:4.11.1' // Simulation Android
    testImplementation 'junit:junit:4.13.2' // JUnit core
    
    // Tests d'instrumentation (émulateur/appareil)
    androidTestImplementation libs.ext.junit    // AndroidJUnit4
    androidTestImplementation libs.espresso.core // Espresso UI testing
}
```

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

## Configuration de build

### Android Gradle Plugin (AGP)

**Version recommandée** : 8.13.0 (AGP 9.0.0 non supporté par Robolectric)

**build.gradle (Project)** :
```groovy
plugins {
    alias(libs.plugins.android.application) apply false
}
```

### Namespace & SDK

**build.gradle (Module : app)** :
```groovy
android {
    namespace 'com.uphf.sae6_app'
    compileSdk 36              // Android 15
    
    defaultConfig {
        applicationId "com.uphf.sae6_app"
        minSdk 30               // Android 11 minimum
        targetSdk 36            // Android 15 (dernière version)
        versionCode 1
        versionName "1.0"
    }
}
```

**Compatibilité** :
- Min SDK 30 (API 30 = Android 11)
- Target SDK 36 (API 36 = Android 15)
- Compile SDK 36

### Options de compilation Java

```groovy
compileOptions {
    sourceCompatibility JavaVersion.VERSION_11  // Java 11
    targetCompatibility JavaVersion.VERSION_11  // Java 11
}
```

### Build types

```groovy
buildTypes {
    release {
        minifyEnabled false           // ProGuard désactivé
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'),
                      'proguard-rules.pro'
    }
}
```

---

## Évolutions futures et points d'extension

### 1. Persistance de données

**Objectif** : Sauvegarder progression utilisateur

**Options recommandées** :
```java
// Option A : SharedPreferences (simple)
SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
prefs.edit().putInt("score", 100).apply();

// Option B : Room Database (complexe, scalable)
@Entity
public class User {
    @PrimaryKey public int id;
    public String name;
    public int score;
}

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase { }
```

**Points d'extension** :
- `HomeActivity.onCreate()` → charger données utilisateur depuis DB
- `QuizActivity.onAnswerSelected()` → logger réponses en DB
- `DashboardActivity.recalcImpact()` → sauvegarder calculs

### 2. Architecture MVVM/MVP

**Actuellement** : Activities contiennent logique métier

**Évolution proposée** :
```
Activity (UI)
    ↓
ViewModel / Presenter (Logique)
    ↓
Repository (Données)
    ↓
Database / API
```

**Bénéfices** :
- Testabilité améliorée
- Séparation concerns
- Réutilisabilité code

### 3. Intégration backend API

**Cas d'usage** :
- Charger questions quiz depuis serveur
- Synchroniser progression utilisateur
- Récupérer nouvelles fiches info

**Framework recommandé** : Retrofit + OkHttp
```java
// Interface API
public interface GreenItApi {
    @GET("/api/questions")
    Call<List<QuizItem>> getQuestions(@Query("theme") String theme);
}

// Utilisation
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.greenitapp.com")
    .addConverterFactory(GsonConverterFactory.create())
    .build();

GreenItApi service = retrofit.create(GreenItApi.class);
service.getQuestions("climat").enqueue(callback);
```

### 4. Système de points et badges

**Modèle** :
```java
public class Achievement {
    String id;           // "first_quiz"
    String name;         // "Premier Quiz"
    int points;          // 100
    boolean unlocked;    // true/false
}
```

**Logique** :
- Débloquer achievement après actions (quiz réussi, 10 fiches lues)
- Calculer total points
- Afficher dans ProfileActivity

### 5. Graphiques et visualisations

**Bibliothèque** : MPAndroidChart
```gradle
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
```

**Cas d'usage** :
- Historique impact CO2 (graph ligne)
- Répartition par thème quiz (pie chart)
- Progression utilisateur (bar chart)

### 6. Support multilingue

**Actuellement** : Français uniquement (`strings.xml`)

**Évolution** :
```
res/values/strings.xml          → Français
res/values-en/strings.xml       → Anglais
res/values-es/strings.xml       → Espagnol
```

**Changer langue** :
```java
Locale locale = new Locale("en");
Locale.setDefault(locale);
Configuration config = new Configuration();
config.locale = locale;
getResources().updateConfiguration(config, getResources().getDisplayMetrics());
```

### 7. Tests améliorés

**Actuellement** : Tests unitaires avec Robolectric

**À ajouter** :
```java
// Tests unitaires activités
@RunWith(RobolectricTestRunner.class)
public class DashboardActivityTest {
    @Test
    public void testCo2Calculation() {
        // Assert calcul CO2
    }
}

// Tests UI avec Espresso
@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {
    @Test
    public void testNavigationToDashboard() {
        onView(withId(R.id.card_dashboard)).perform(click());
        intended(hasComponent(DashboardActivity.class));
    }
}

// Tests d'intégration
public class IntegrationTest {
    // Teste flux complet : HomeActivity → Quiz → Dashboard
}
```

### 8. Notifications push

**Cas d'usage** :
- Rappels quotidiens de nettoyage e-mails
- Badges achievements débloqués
- Nouvelles fiches disponibles

**Framework** : Firebase Cloud Messaging (FCM)
```gradle
implementation 'com.google.firebase:firebase-messaging:23.2.1'
```

### 9. Thème sombre (Dark Mode)

```xml
<!-- res/values-night/colors.xml -->
<color name="primary">@color/dark_primary</color>

<!-- styles.xml -->
<style name="Theme.SAE6_APP" parent="Theme.MaterialComponents.DayNight">
    <!-- Material 3 supporte dark mode auto -->
</style>
```

### 10. Accessibilité

**Points d'amélioration** :
- Ajouter descriptions texte aux images (android:contentDescription)
- Tester avec TalkBack (lecteur écran)
- Assurer contraste couleurs suffisant (WCAG AA)

---

## Résumé architecture

| Aspect | Détails |
|--------|---------|
| **Pattern** | Activity-based, Intent-driven |
| **Langage** | Java 11 |
| **SDK Min** | API 30 (Android 11) |
| **SDK Target** | API 36 (Android 15) |
| **Persistance** | Aucune (à implémenter) |
| **API Distance** | Local (pas de backend) |
| **Tests** | Robolectric unitaire + Espresso UI |
| **Librairies** | AndroidX, Material Design |
| **État projet** | MVP (features core + placeholders) |

---

## Conclusion

L'application SAE 6 Green IT présente une architecture simple et claire, adaptée à un projet éducatif. Sa structure Activity-based facilite l'ajout de nouvelles fonctionnalités et permet une évolution progressive vers des patterns plus avancés (MVVM, dépendance injection) et une intégration backend.

Les points d'extension identifiés couvrent persistance, architecture, backend, gamification, et accessibilité, offrant une feuille de route pour les évolutions futures tout en maintenant une base de code compréhensible et maintenable.


