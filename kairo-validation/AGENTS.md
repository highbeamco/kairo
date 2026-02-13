# kairo-validation

Email address validation following the WHATWG HTML Standard. Syntactic check only.

## Key files
- `src/main/kotlin/kairo/validation/Validator.kt` -- `Validator.emailAddress` regex property

## Patterns and conventions
- The regex follows https://html.spec.whatwg.org/multipage/input.html#valid-e-mail-address
- Access via `Validator.emailAddress.matches(input)`
- `Validator` is an object; add new validators as additional properties

## Foot-guns
- This is a syntactic check only; technically valid but non-existent addresses will pass
- The regex does not support internationalized email addresses (non-ASCII local parts or domains)
- The WHATWG spec is intentionally more permissive than RFC 5322
