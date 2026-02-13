# kairo-slack

Slack integration as a Feature. Wraps Slack's Java SDK with a Kotlin-friendly `SlackClient` that provides channel name-to-ID mapping.

## Key files
- `src/main/kotlin/kairo/slack/SlackClient.kt` -- wrapper around `Slack.methodsAsync()` with `channels` map; implements `AutoCloseable`
- `feature/src/main/kotlin/kairo/slack/SlackFeature.kt` -- Feature that creates and binds the `SlackClient` into Koin

## Submodules
- `:feature` -- `SlackFeature` and config

## Patterns and conventions
- Inject `SlackClient` from Koin
- Refer to channels by logical name: `slackClient.channels["alerts"]` returns the Slack channel ID
- Token is stored as `ProtectedString`

## Foot-guns
- `SlackClient` implements `AutoCloseable` via the underlying `Slack` instance; ensure proper lifecycle management

## Related modules
- **kairo-dependency-injection** -- `SlackClient` is bound in Koin via `HasKoinModules`
- **kairo-protected-string** -- token is stored as `ProtectedString`
