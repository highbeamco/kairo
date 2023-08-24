package limber.config.event

public data class EventConfig(
  val projectName: String,
  val publish: Publish? = null,
  val subscribe: Subscribe? = null,
) {
  public data class Publish(
    val shutdownMs: Long,
  )

  public data class Subscribe(
    val maxOutstandingElements: Long,
    val maxOutstandingBytes: Long,
    val shutdownMs: Long,
    val startupMs: Long,
  )
}
