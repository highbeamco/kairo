package kairo.googleCloudTasks

public class NoopGoogleCloudTasks : GoogleCloudTasks() {
  override suspend fun create(task: Task): Unit = Unit

  override fun close(): Unit = Unit
}
