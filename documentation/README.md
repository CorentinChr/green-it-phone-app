# Application Mobile SAE 6 - Green IT

Application Android éducative dédiée à la sensibilisation aux pratiques écologiques et au numérique responsable, développée dans le cadre de la SAE "Évolution d'une application existante" de semestre 6 du BUT Informatique de l'IUT de Maubeuge.

## Objectif

L'application vise à encourager les utilisateurs à adopter des comportements écoresponsables à travers :
- Un **quiz de positionnement** (10 questions) qui détermine un niveau utilisateur (débutant/intermédiaire/difficile)
- Un **tableau de bord** d'estimation d'impact CO2e enrichi (mails, matériel, services numériques)
- Un **quiz thématique** (questions chargées depuis API), filtré par thème et difficulté
- Des **fiches d'information interactives** (texte, image, étapes, mini-quiz)
- Une **visualisation de données Green IT** avec niveaux de détail selon le profil
- Un **suivi de progression** basé sur l'historique des scores
- Un **profil utilisateur** persistant (nom + niveau)

---

## Technologies

- **Langage** : Java
- **Build tool** : Gradle
- **Android Gradle Plugin (catalogue de versions)** : 9.0.1
- **SDK Android** : minSdk 30, targetSdk 36, compileSdk 36
- **Réseau** : Retrofit + Gson + OkHttp (logging interceptor)
- **Graphiques** : MPAndroidChart
- **Tests unitaires Android** : JUnit4 + Robolectric

---

### Prérequis

- Android Studio récent (avec support AGP/Gradle compatible)
- JDK 11+
- Android SDK Platform 36
- Connexion internet (pour les appels API vers le backend Railway)

> Note compatibilité : la version AGP déclarée actuellement est `9.0.1` (`gradle/libs.versions.toml`). Si Android Studio signale une incompatibilité AGP, aligner temporairement vers la version AGP maximale supportée par l'IDE.

---

### Installation

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/CorentinChr/green-it-phone-app.git
   cd SAE6_APP
   ```

2. Ouvrir le projet dans Android Studio

3. Synchroniser Gradle (`File > Sync Project with Gradle Files`)

4. Vérifier la configuration des versions (`gradle/libs.versions.toml` et `app/build.gradle`)

---

## Exécution

### Sur émulateur

1. Créer un AVD via `Tools > Device Manager`
2. Lancer l'émulateur
3. Cliquer sur `Run 'app'` (ou `Shift+F10`)

### Sur appareil physique

1. Activer les options développeur
2. Activer le débogage USB
3. Connecter l'appareil
4. Sélectionner l'appareil et lancer l'application

---

## Structure du projet

```
app/src/main/java/com/uphf/sae6_app/
├── MainActivity.java                   # Activity Android générique (layout edge-to-edge)
├── HomeActivity.java                   # Écran d'accueil + déverrouillage post-quiz de niveau
├── QuizLevelActivity.java              # Quiz de positionnement (score /10, niveau)
├── QuizLevelResultActivity.java        # Résultat du quiz de positionnement
├── DashboardActivity.java              # Estimation impact CO2e + pie chart catégories
├── QuizActivity.java                   # Quiz thématique (API + filtres thème/difficulté)
├── InfoActivity.java                   # Fiches thématiques (étapes + mini-quiz)
├── DataVizActivity.java                # Liste de données Green IT (API + filtrage catégorie)
├── DataVizAdapter.java                 # Adaptateur RecyclerView selon niveau utilisateur
├── ProgressActivity.java               # Historique/moyenne des scores (quiz et quiz de niveau)
├── ProfileActivity.java                # Gestion du nom et niveau utilisateur
├── ScoreStorage.java                   # Utilitaire SharedPreferences (historique JSON)
├── model/
│   ├── QuizItem.java                   # Modèle question quiz
│   ├── InfoItem.java                   # Modèle fiche/étape/quiz intégré
│   └── GreenItData.java                # Modèle data visualisation
└── retrofit/
    ├── RetrofitClient.java             # Configuration client HTTP
    ├── GreenItApi.java                 # Endpoints quiz/fiches/data
    ├── ApiService.java                 # Endpoints utilisateurs
    └── dto/                            # UserRequest / UserResponse
```

---

## Fonctionnalités implémentées

### Quiz de positionnement et verrouillage de l'accueil
- Quiz de test en 10 questions pour déterminer le niveau utilisateur
- Persistance du score et du niveau dans `SharedPreferences` (`prefs_user`)
- Déverrouillage des cartes principales sur l'écran d'accueil après validation
- Écran de résultat dédié (`QuizLevelResultActivity`)

### Tableau de bord impact CO2e
- Estimation globale sur trois catégories : mails, matériel, services
- Paramètres utilisateur : fréquence de nettoyage, désabonnement, nombre d'appareils, heures de streaming
- Choix qualité vidéo (SD/HD/UHD) avec impact sur le calcul services
- Diagramme circulaire MPAndroidChart de répartition par catégorie

### Quiz et fiches thématiques via API
- Chargement des questions et fiches depuis le backend (`RetrofitClient`)
- Filtrage par thème (`energie`, `dechets`, `numerique`, `mobilite`)
- Filtrage par difficulté lié au niveau utilisateur
- Feedback immédiat sur les réponses et navigation séquentielle

### Visualisation de données Green IT
- Écran `DataVizActivity` basé sur `RecyclerView`
- Deux catégories de lecture : fabrication et usage
- Niveau d'information progressif selon le profil utilisateur
- Données récupérées via endpoint `greenitdata`

### Progression et profil
- Historique des scores (5 derniers) pour quiz et quiz de niveau
- Calcul de moyenne et message "prêt à passer au niveau supérieur"
- Modification/sauvegarde du nom et du niveau utilisateur

---

## Tests

Des tests unitaires Robolectric sont présents dans `app/src/test/java/com/uphf/sae6_app`.

### Exécuter les tests

Depuis la racine du projet :
```powershell
.\gradlew.bat testDebugUnitTest
```

Ou pour tout lancer :
```powershell
.\gradlew.bat test
```

Rapport HTML principal : `app/build/reports/tests/testDebugUnitTest/index.html`

---

### Tests implémentés (principaux)

- **DashboardActivityTest** : calculs dashboard (méthodes internes)
- **DataVizActivityTest** / **DataVizAdapterTest** : initialisation UI + adaptation des niveaux
- **HomeActivityTest** : logique de verrouillage et navigation quiz de niveau
- **QuizLevelActivityTest** / **QuizLevelResultActivityTest** : stockage score/niveau + écran résultat
- **QuizActivityTest** : normalisation de thème
- **InfoActivityTest** : niveau utilisateur dérivé des préférences
- **ScoreStorageTest** : stockage, moyenne, formatage historique
- **ProfileActivityTest** / **ProgressActivityTest** / **MainActivityTest** : comportements UI de base

> `QuizItemTest` et `InfoItemTest` existent mais ne contiennent pas encore de cas de test actifs.

---

## Membres du projet

- DE-CLERCK Rafael
- CHARPIOT Corentin
- HENICHARD Théo
- BOUJU Maxime
- DELILLE Yannis

## Licence

Projet académique développé dans le cadre de la SAE 6 - BUT Informatique - IUT de Maubeuge (UPHF)

#### Pour plus d'informations, veuillez consulter la documentation technique (TECHNICAL_DOCUMENTATION.md)


