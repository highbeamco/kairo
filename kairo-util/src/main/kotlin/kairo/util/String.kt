package kairo.util

import java.text.Normalizer

/**
 * Normalizes an arbitrary string to lowercase Latin characters and digits.
 * Strips diacritics, removes non-alphanumeric characters, and collapses whitespace.
 *
 * ```
 * canonicalize(" Con  | dãnas^t") // => "con danast"
 * ```
 */
public fun canonicalize(subject: String): String {
  var result = Normalizer.normalize(subject, Normalizer.Form.NFD)
  result = result.lowercase()
  result = Regex("\\s").replace(result, " ")
  result = Regex("[-/._|+=@#:]").replace(result, " ")
  result = Regex("[^ a-z0-9]").replace(result, "")
  result = Regex(" +").replace(result, " ")
  result = result.trim()
  return result
}

/**
 * Like [canonicalize], but joins words with the given [delimiter] instead of spaces.
 *
 * ```
 * slugify(" Con  | dãnas^t", delimiter = "-") // => "con-danast"
 * ```
 */
public fun slugify(subject: String, delimiter: String): String =
  canonicalize(subject).replace(" ", delimiter)
