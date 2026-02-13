# kairo-server

Server container for Features. Executes Feature lifecycles with priority-based ordering: start runs low-to-high priority, stop runs high-to-low.

## Key files
- `src/main/kotlin/kairo/server/Server.kt` -- `Server` class with `start()`, `stop()`, lifecycle management
- `src/main/kotlin/kairo/server/ServerState.kt` -- state enum: Default, Starting, Running, Stopping

## Patterns and conventions
- Construct with `Server(name, features)` where `features` is an ordered list of `Feature` instances
- `start()` uses `coroutineScope` for fail-fast: if any handler fails, all are cancelled
- `stop()` uses `supervisorScope` for best-effort: if any handler fails, remaining handlers still run
- Server attempts cleanup (`onStop()`) if startup fails partway through
- State transitions are protected by a `Mutex` lock; `state` is `@Volatile` for lock-free reads

## Foot-guns
- Handlers at the same priority run concurrently; they must be thread/coroutine-safe
- `start()` can only be called once from `Default` state; calling it again will throw
- If startup fails, the server returns to `Default` state after attempting cleanup

## Related modules
- **kairo-feature** -- `Feature` and `LifecycleHandler` define what the Server executes
- **kairo-application** -- `kairo {}` block creates and runs the Server
