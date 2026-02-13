# Kairo agent guide

Kairo is a production-ready Kotlin backend toolkit.
This monorepo contains standalone and application libraries
organized under a single Gradle build.

## Build and test

```bash
./gradlew build              # Full build + tests + linting.
./gradlew compileKotlin      # Compile only (fast check for KDoc and code changes).
./gradlew detektMain detektTest  # Run static analysis on main and test sources.
./gradlew test               # Run all tests.
./gradlew check              # Run all verification tasks (tests + detekt).
./gradlew assemble           # Build all artifacts without running tests.
```

JDK 21 (Corretto) is required. Gradle wrapper handles the rest.

## Project structure

- **Standalone libraries** (`kairo-config`, `kairo-id`, `kairo-money`, etc.):
  Can be used independently in any Kotlin project.
- **Application libraries** (`kairo-feature`, `kairo-server`, `kairo-rest`, etc.):
  Help when building a full Kairo application using the Feature/Server model.
- **BOM modules** (`bom/`, `bom-full/`):
  Version-aligned dependency management.
- **Build infrastructure** (`buildSrc/`):
  Custom Gradle plugins (`kairo-library`, `kairo-library-publish`).
- **Documentation** (`docs/`):
  Starlight-based docs site. Do not edit generated files under `docs/src/content/docs/libraries/`.

## Architecture overview

See [ARCHITECTURE.md](./ARCHITECTURE.md) for the full picture. Key concepts:

- **Features** are the core building block. Framework Features (REST, SQL, DI) and Domain Features (your app code) compose together.
- **Servers** are containers of Features with priority-based lifecycle management.
- The `kairo {}` block in `kairo-application` is the entrypoint.

## Style guide

Follow the [style guide](./docs/src/content/docs/style-guide.md) and the [Google Style Guide](https://developers.google.com/style). Key rules:

- **Proper nouns:** Capitalize "Feature", "Server", and "Flow" when referring to Kairo/Kotlin concepts.
- **Sentence case** in all headings and titles.
- **Full sentences** with proper punctuation, including in comments, logs, and list items with multiple clauses.
- **Ordering:** Read-create-update-delete for members. Alphabetical for lists with no natural order.
- **Singular** names for packages, folders, and Features. REST paths are plural.
- **Avoid abbreviations.** Do not use "i.e." or "e.g.".
- **"Non-null"** not "not null".
- **Get/list/search:** "Get" for a single entity, "list" for all matching a simple condition, "search" for filtered queries.

## Code conventions

- `explicitApi()` is enabled: all public declarations need explicit visibility modifiers.
- All warnings are treated as errors.
- KDoc should focus on the "why", not the "what". Call out foot-guns, threading requirements, and surprising behavior. Do not add boilerplate KDoc that restates the obvious.
- Detekt is used for static analysis. Configuration is in `.detekt/`.

## Module layout

Each module follows this structure:

```
kairo-<name>/
  build.gradle.kts
  README.md
  AGENTS.md          # Module-specific agent context.
  CLAUDE.md -> AGENTS.md
  src/main/kotlin/kairo/<name>/
  src/test/kotlin/kairo/<name>/
```

Some modules have submodules (for example `kairo-rest:feature`, `kairo-sql:postgres`).
These live as subdirectories within the parent module.

## Key files

- `settings.gradle.kts` — all module includes.
- `build.gradle.kts` (root) — Dokka aggregation config.
- `buildSrc/` — shared Gradle plugins and conventions.
- `docs/modules.json` — module categorization for the docs site.
- `ARCHITECTURE.md` — how Features, Servers, and the lifecycle model work.
- `GETTING_STARTED.md` — onboarding guide for new users.
