package kairo.money

import javax.money.NumberValue

/**
 * Reified wrapper for [NumberValue.numberValueExact] that avoids the precision loss
 * you'd get from [NumberValue.doubleValue] or [NumberValue.longValue].
 */
public inline fun <reified T : Number> NumberValue.numberValueExact(): T =
  numberValueExact(T::class.java)
