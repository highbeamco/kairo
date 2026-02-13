package kairo.util

import com.google.common.io.Resources
import java.net.URL

/** Loads a classpath resource URL using Guava. Requires Guava on the runtime classpath. */
public fun resource(resourceName: String): URL =
  Resources.getResource(resourceName)
