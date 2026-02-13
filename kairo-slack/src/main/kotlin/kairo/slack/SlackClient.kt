package kairo.slack

import com.slack.api.Slack
import com.slack.api.methods.AsyncMethodsClient
import kairo.protectedString.ProtectedString

/**
 * Slack API client that delegates to Slack's [AsyncMethodsClient].
 * Provides named channel mapping via [channels].
 */
@OptIn(ProtectedString.Access::class)
public class SlackClient(
  slack: Slack,
  token: ProtectedString,
  /** Mapping of logical channel names to Slack channel IDs. */
  public val channels: Map<String, String>,
) : AsyncMethodsClient by slack.methodsAsync(token.value), AutoCloseable by slack
