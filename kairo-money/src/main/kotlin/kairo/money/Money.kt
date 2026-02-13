package kairo.money

import javax.money.Monetary
import org.javamoney.moneta.Money

/**
 * Rounds using javax.money's default rounding, which is HALF_UP
 * to the currency's standard decimal places (e.g. 2 for USD, 0 for JPY).
 * The rounding behavior varies depending on the currency.
 */
public fun Money.round(): Money =
  with(Monetary.getDefaultRounding())
