# kairo-protected-string

Lightweight wrapper for sensitive strings that prevents accidental exposure in logs and stack traces. `toString()` returns `"ProtectedString(value='REDACTED')"`.

## Key files
- `src/main/kotlin/kairo/protectedString/ProtectedString.kt` -- the wrapper class with `@RequiresOptIn` access control

## Patterns and conventions
- Creating or reading a `ProtectedString` value requires `@OptIn(ProtectedString.Access::class)`
- Jackson support is built in via `@JsonCreator` and `@JsonValue` annotations
- `equals()` and `hashCode()` delegate to the underlying string value
- Use this for passwords, API keys, tokens, and any secret that should not appear in logs

## Foot-guns
- This is NOT encryption; the value is stored in plain text in memory
- Forgetting `@OptIn(ProtectedString.Access::class)` will cause a compiler error, which is intentional
- Serialization via Jackson will output the raw value; ensure transport-level security (HTTPS, etc.)

## Related modules
- **kairo-gcp-secret-supplier** -- returns `ProtectedString` from Secret Manager
- **kairo-config** -- config resolvers that fetch secrets typically return `ProtectedString`
