# kairo-money

javax.money (JSR 354) with Moneta, plus a Jackson module for Money and CurrencyUnit serialization. Provides extension functions and customizable serialization formats.

## Key files
- `src/main/kotlin/kairo/money/Money.kt` -- `Money.round()` extension using `Monetary.getDefaultRounding()`
- `src/main/kotlin/kairo/money/MoneyFormat.kt` -- abstract class for custom serialization; `MoneyFormat.Default` serializes as `{amount, currency}`
- `src/main/kotlin/kairo/money/MoneyModule.kt` -- Jackson `SimpleModule` registering Money and CurrencyUnit serializers/deserializers

## Patterns and conventions
- Register `MoneyModule()` with your `KairoJson` instance to enable Money serialization
- Extend `MoneyFormat` to customize how Money is serialized (e.g. as a string, as cents)
- Default format serializes Money as `{"amount": 10.00, "currency": "USD"}`
- `MoneyModule.Builder` lets you swap the `MoneyFormat` at registration time

## Foot-guns
- Forgetting to register `MoneyModule` will cause Jackson to fail on Money fields
- `Money.round()` uses the default rounding for the currency (HALF_UP to the currency's standard decimal places); behavior varies by currency
- `numberValueExact()` is used in serialization; amounts that cannot be exactly represented will throw

## Related modules
- **kairo-serialization** -- provides the `KairoJson` wrapper where `MoneyModule` is registered
