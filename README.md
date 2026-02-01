# BaristaAI

[![CI](https://github.com/luczka/BaristaAI/actions/workflows/ci.yml/badge.svg)](https://github.com/luczka/BaristaAI/actions/workflows/ci.yml)

## Table of Contents

- [Project Description](#project-description)
- [Tech Stack](#tech-stack)
- [Getting Started Locally](#getting-started-locally)
- [Available Scripts](#available-scripts)
- [Project Scope](#project-scope)
- [Project Status](#project-status)
- [License](#license)

## Project Description

**BaristaAI** is an Android app (min API 30) that helps beginners brew coffee with alternative methods such as V60, Chemex, Aeropress, and Moka. The main value is quickly generating recipes from simple inputs (method, bean weight, temperature control) and saving them for later use. The MVP is minimal, online-only, and offers:

- **AI-generated recipes** — Up to 3 recipes per request (one base and two alternatives) via OpenRouter.ai.
- **Recipe management** — Save, edit, and delete recipes; manual entry with the same fields as generated recipes.
- **Card-based workflow** — View generated recipes on cards with accept, reject, or edit actions.
- **Similar-recipe detection** — When adding a recipe, the app compares with saved ones (method, coffee amount, ratio, temperature) to reduce duplicates.
- **Simple auth** — Sign up and sign in with email/password or Google Sign-In.

Recipes include at least coffee amount, water, ratio, and temperature; they can include an assistant tip, and generation requests can include a user comment. Accepted or rejected recipes are logged for model quality evaluation. AI drafts are retained for 7 days.

## Tech Stack

| Layer | Technologies |
|-------|--------------|
| **Frontend (Android)** | Kotlin, Jetpack Compose, Material 3, Hilt, Ktor, AndroidX Navigation Compose, Kotlinx Serialization |
| **Backend** | Supabase (PostgreSQL, PostgREST), Supabase Kotlin SDK, Row Level Security (RLS) |
| **AI** | OpenRouter.ai |
| **CI/CD** | GitHub Actions |

**Key dependencies (from version catalog):** Android Gradle Plugin 8.13.2, Kotlin 2.1.20, Hilt 2.56.2, Compose BOM, Supabase 3.0.3, Ktor 3.0.3, Credentials/Play Services Auth, Google ID. Testing: JUnit, Mockk, Turbine, Espresso.

## Getting Started Locally

### Prerequisites

- **JDK 17** (CI uses Temurin 17)
- **Android Studio** (recommended) or Android SDK with API 30+ and build-tools
- **Gradle** — use the project wrapper: `./gradlew` (Gradle 8.13)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/luczka/BaristaAI.git
   cd BaristaAI
   ```

2. **Configure `local.properties`** in the project root with your Supabase and Google credentials:
   ```properties
   sdk.dir=/path/to/your/Android/sdk
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_ANON_KEY=your-supabase-anon-key
   GOOGLE_WEB_CLIENT_ID=your-google-web-client-id.apps.googleusercontent.com
   ```
   These are injected as `BuildConfig` fields and are required for auth and backend.

3. **Build and run**
   - From the command line:
     ```bash
     ./gradlew assembleDebug
     ```
   - Or open the project in Android Studio and run the **app** configuration on an emulator or device (API 30+).

### Backend (Supabase)

- Use the Supabase project linked to `SUPABASE_URL` and `SUPABASE_ANON_KEY`.
- Database schema and policies are under `supabase/migrations/`.
- Edge functions for recipe generation are in `supabase/functions/` (e.g. `generate-recipes`, `generate-recipes-en`).

## Available Scripts

All commands use the Gradle wrapper from the project root:

| Command | Description |
|---------|-------------|
| `./gradlew assembleDebug` | Build debug APK |
| `./gradlew assembleRelease` | Build release APK |
| `./gradlew testDebugUnitTest` | Run unit tests |
| `./gradlew lintDebug` | Run Android lint |

**GitHub Actions**

- **CI** (`.github/workflows/ci.yml`) — On pull requests and pushes to `main`: JDK 17, lint, unit tests, assemble debug APK, upload APK artifact.
- **CD** (`.github/workflows/cd-android.yml`) — On version tags (`v*`) or manual dispatch: assemble release APK and upload artifact.

## Project Scope

**In scope (MVP)**

- Generate up to 3 recipes per request (base + 2 alternatives); recipe fields: coffee, water, ratio, temperature; optional tip and user comment.
- Present recipes on cards with accept, reject, edit; logging accept/reject for model quality.
- Manual recipe creation and full edit/delete for saved recipes.
- List saved recipes with navigation to detail and edit.
- Similar-recipe detection when adding (method, coffee, ratio, temperature).
- Email/password and Google Sign-In; sign out.
- Clear messaging for no connection and invalid or incomplete input.

**Out of scope (MVP)**

- Spaced repetition or planning; linking coffee beans to recipes; sharing recipe sets.
- Web or iOS apps; offline mode; onboarding; password reset or email verification.
- Predefined rules for differentiating alternatives beyond model logic.

## Project Status

- **Version:** 1.0 (versionCode 1, versionName `"1.0"`).
- **Phase:** MVP; online-only Android app.
- **Build:** minSdk 30, targetSdk 36.

## License

This project does not include a license file. All rights reserved.
