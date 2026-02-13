# kairo-mailersend

MailerSend email integration as a Feature. Wraps the MailerSend Java SDK with a Kotlin-friendly `Mailer` class that dispatches blocking SDK calls to `Dispatchers.IO`.

## Key files
- `src/main/kotlin/kairo/mailersend/Mailer.kt` -- wrapper with `use {}` block that runs MailerSend operations on IO dispatcher
- `feature/src/main/kotlin/kairo/mailersend/MailersendFeature.kt` -- Feature that creates and binds the `Mailer` into Koin

## Submodules
- `:feature` -- `MailersendFeature` and config

## Patterns and conventions
- Inject `Mailer` from Koin, then use `mailer.use { emails().send(email) }` to send emails
- `Mailer.templates` maps logical template names to MailerSend template IDs
- Configure with API token (as `ProtectedString`) and template map

## Foot-guns
- The MailerSend SDK is blocking; always use `mailer.use {}` to ensure IO dispatcher is used

## Related modules
- **kairo-dependency-injection** -- `Mailer` is bound in Koin via `HasKoinModules`
- **kairo-protected-string** -- API token is stored as `ProtectedString`
