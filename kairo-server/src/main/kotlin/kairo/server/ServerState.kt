package kairo.server

/**
 * Represents the lifecycle state of a [Server].
 * [Default] is both the initial and final state.
 */
public enum class ServerState {
  Default,
  Starting,
  Running,
  Stopping,
}
