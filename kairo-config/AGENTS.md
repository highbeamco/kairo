# kairo-config

HOCON-based configuration with multi-environment support, environment variable substitution, and dynamic config resolvers that can pull values from external sources like Google Secret Manager.

## Key files
- `src/main/kotlin/kairo/config/ConfigLoader.kt` -- loads HOCON files, applies resolvers, deserializes to typed config
- `src/main/kotlin/kairo/config/ConfigResolver.kt` -- prefix-based dynamic value resolution
- `src/main/kotlin/kairo/config/Environment.kt` -- enum for dev/staging/prod environments

## Patterns and conventions
- Config files use HOCON format and follow the naming convention `config.conf`, `config-dev.conf`, etc.
- Resolvers match string values by prefix and replace the entire value with the resolver's result
- Environment variable substitution uses HOCON's built-in `${?ENV_VAR}` syntax
- `ALLOW_COERCION_OF_SCALARS` is enabled in the config deserializer (unlike default KairoJson) to support env var string coercion

## Foot-guns
- Resolvers run after HOCON loading; if a resolver returns null, the original value (with prefix stripped) is used
- The config JSON mapper has `allowUnknown = true`, which differs from the default KairoJson behavior

## Related modules
- **kairo-hocon** -- HOCON parsing and JSON bridge
- **kairo-serialization** -- `KairoJson` used for config deserialization
- **kairo-gcp-secret-supplier** -- common resolver source for secrets
- **kairo-protected-string** -- config values that are secrets should use `ProtectedString`
