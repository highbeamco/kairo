# kairo-dependency-injection

Koin-based dependency injection integrated with Kairo's Feature lifecycle.

## Key files
- `feature/src/main/kotlin/kairo/dependencyInjection/DependencyInjectionFeature.kt` -- Feature that initializes Koin and scans Features for `HasKoinModules`
- `feature/src/main/kotlin/kairo/dependencyInjection/HasKoinModules.kt` -- interface for Features that provide Koin modules

## Submodules
- `:feature` -- `DependencyInjectionFeature` and `HasKoinModules`

## Patterns and conventions
- Add `DependencyInjectionFeature(koinApplication)` to your Server's Feature list
- Implement `HasKoinModules` on your Feature and provide `koinModules`
- Use `@Single` and `@Factory` annotations with Koin's KSP compiler for automatic module generation
- Constructor parameters are automatically injected by Koin
- DI runs at priority `-100,000,000` (before everything else)

## Foot-guns
- `HasKoinModules` is scanned at DI startup; if a Feature doesn't implement it, its bindings won't be registered
- Koin annotation processing requires the `com.google.devtools.ksp` Gradle plugin

## Related modules
- **kairo-feature** -- Features implement `HasKoinModules` to provide bindings
- **kairo-integration-testing** -- `KoinExtension` provides parameter injection in tests
