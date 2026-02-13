# Kairo-Ktor

This module is intended only for internal use.

It provides `KairoConverter`, a Ktor `ContentConverter` adapted from Ktor's built-in `JacksonConverter`.
The key difference is that `KairoConverter` preserves full generic type information
(via [kairo-reflect](../kairo-reflect/README.md))
instead of losing it to JVM type erasure.

This is what allows Kairo's REST layer to correctly serialize and deserialize
generic types, polymorphic sealed classes, and other cases where Jackson needs complete type info.

You should not need to depend on this module directly.
It is pulled in automatically by [kairo-rest](../kairo-rest/README.md).
