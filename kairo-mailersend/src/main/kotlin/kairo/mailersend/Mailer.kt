package kairo.mailersend

import com.mailersend.sdk.MailerSend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Wrapper around the MailerSend SDK that dispatches API calls to [Dispatchers.IO]. */
public class Mailer(
  private val mailersend: MailerSend,
  /** Mapping of logical template names to MailerSend template IDs. */
  public val templates: Map<String, String>,
) {
  /** Executes a MailerSend SDK operation on [Dispatchers.IO]. */
  public suspend fun use(block: MailerSend.() -> Unit) {
    withContext(Dispatchers.IO) {
      mailersend.block()
    }
  }
}
