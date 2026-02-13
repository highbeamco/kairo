# kairo-gcp-secret-supplier

Lightweight, coroutine-friendly Google Secret Manager wrapper. Returns secrets as `ProtectedString`.

## Key files
- `src/main/kotlin/kairo/gcpSecretSupplier/GcpSecretSupplier.kt` -- main class that fetches secrets from GCP
- `testing/src/main/kotlin/kairo/gcpSecretSupplier/FakeGcpSecretSupplier.kt` -- in-memory fake for tests

## Submodules
- `:testing` -- provides `FakeGcpSecretSupplier` for tests

## Patterns and conventions
- Use as a config resolver to dynamically fetch secrets during config loading
- Secrets are returned as `ProtectedString` to prevent accidental logging

## Foot-guns
- Requires GCP credentials at runtime (Application Default Credentials or service account)
- The testing fake stores secrets in a plain `Map`; it does not simulate GCP behavior like IAM or versioning

## Related modules
- **kairo-config** -- `GcpSecretSupplier` is typically used as a `ConfigResolver`
- **kairo-protected-string** -- secrets are wrapped in `ProtectedString`
