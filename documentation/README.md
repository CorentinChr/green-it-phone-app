# Application Mobile SAE 6 - Green IT

Application Android éducative dédiée à la sensibilisation aux pratiques écologiques et au numérique responsable, développée dans le cadre de la SAE "Évolution d'une application existante"  de semestre 6 du BUT Informatique de l'IUT de Maubeuge.

## Objectif

L'application vise à encourager les utilisateurs à adopter des comportements écoresponsables à travers :
- Un **tableau de bord** permettant d'estimer l'impact carbone lié au stockage d'e-mails
- Un **quiz interactif** sur les thématiques environnementales (climat, énergie, déchets)
- Des **fiches d'information** pratiques sur les gestes écologiques
- Un **suivi de progression**
- Un **profil utilisateur**

---

## Technologies

- **Langage** : Java
- **Build tool** : Gradle
- **Version Android Gradle Plugin** : 8.13.0 
- **Minimum SDK** : API 21

---

### Prérequis

- Android Studio Panda 1 | 2025.3.1 ou version compatible
- JDK 17 ou supérieur
- Android SDK avec les outils de build appropriés

---

### Installation

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/CorentinChr/green-it-phone-app.git
   cd SAE6_APP
   ```

2. Ouvrir le projet dans Android Studio

3. Synchroniser Gradle (`File > Sync Project with Gradle Files`)

4. Vérifier que la version AGP dans `build.gradle` est compatible (8.13.0 maximum)

---

## Exécution

### Sur émulateur

1. Créer un AVD (Android Virtual Device) via `Tools > Device Manager`
2. Lancer l'émulateur
3. Cliquer sur `Run 'app'` ou utiliser le raccourci `Shift+F10`

### Sur appareil physique

1. Activer le mode développeur sur l'appareil
2. Activer le débogage USB
3. Connecter l'appareil via USB
4. Sélectionner l'appareil dans la liste et lancer l'application

---

## Structure du projet

```
app/src/main/java/com/uphf/sae6_app/
├── MainActivity.java          # Activité principale (point d'entrée)
├── HomeActivity.java          # Écran d'accueil avec navigation
├── DashboardActivity.java     # Tableau de bord impact CO2e
├── QuizActivity.java          # Quiz environnemental
├── InfoActivity.java          # Fiches informatives
├── ProgressActivity.java      # Suivi de progression (placeholder)
├── ProfileActivity.java       # Profil utilisateur (placeholder)
├── QuizItem.java              # Modèle de question
└── InfoItem.java              # Modèle de fiche info
```

---

## Fonctionnalités implémentées

### Tableau de bord
- Calcul simplifié de l'impact carbone du stockage d'e-mails
- Configuration de la fréquence de nettoyage (1-30 jours)
- Option de désabonnement pour réduire le volume de mails
- Visualisation de l'impact estimé en kg CO2e/an

### Quiz
- Questions sur différents thèmes (climat, énergie, déchets)
- Système de filtrage par thème et difficulté (1-3)
- Feedback immédiat avec explications
- Navigation entre questions

### Fiches informatives
- Conseils pratiques pour des gestes écologiques
- Navigation séquentielle
- Support d'images (optionnel)

---

## Tests

Les tests unitaires ont été développés pour valider le bon fonctionnement des différentes activités et modèles de données.

### Exécuter les tests

Pour lancer les tests unitaires :
```bash
./gradlew test
```

Ou depuis Android Studio : `Run > Run 'All Tests'`

---

### Tests implémentés

- **DashboardActivityTest** : Tests du calcul d'impact CO2e et des interactions utilisateur
- **QuizActivityTest** : Tests du chargement des questions, filtrage et validation des réponses
- **InfoActivityTest** : Tests de la navigation et affichage des fiches d'information
- **HomeActivityTest** : Tests de navigation entre les différentes sections
- **MainActivityTest** : Tests d'initialisation de l'activité principale
- **QuizItemTest** : Tests du modèle de données des questions
- **InfoItemTest** : Tests du modèle de données des fiches
- **ProfileActivityTest** : Tests de l'activité profil (placeholder)
- **ProgressActivityTest** : Tests de l'activité progression (placeholder)

Les tests utilisent **Robolectric** pour simuler l'environnement Android sans nécessiter d'émulateur.

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


