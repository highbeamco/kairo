# kairo-application

Entry point for running a Kairo application. The `kairo {}` DSL creates a Server, starts it, waits for JVM termination (via shutdown hook), and cleans up.

## Key files
- `src/main/kotlin/kairo/application/Kairo.kt` -- `kairo {}` DSL entry point

## Patterns and conventions
- `kairo {}` is the top-level entry point; call it from `main()`
- Inside the block, configure your Server with Features and config
- The function blocks until JVM shutdown (SIGTERM, SIGINT)
- Shutdown hooks handle graceful cleanup

## Related modules
- **kairo-server** -- `Server` is created and managed within `kairo {}`
- **kairo-feature** -- Features are composed into the Server
- **kairo-config** -- config is typically loaded within `kairo {}`
