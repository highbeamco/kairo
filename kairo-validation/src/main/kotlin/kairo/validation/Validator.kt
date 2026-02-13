package kairo.validation

/** Common validation patterns. */
@Suppress("MaximumLineLength")
public object Validator {
  /**
   * Validates email addresses per the WHATWG HTML Standard.
   * Deliberately more permissive than RFC 5321.
   * This is a syntactic check only; it does not verify deliverability.
   *
   * See: https://html.spec.whatwg.org/multipage/input.html#valid-e-mail-address
   */
  public val emailAddress: Regex =
    Regex(
      "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:[.][a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$",
    )
}
