# Kairo BOM

The Kairo BOM (Bill of Materials) keeps all your Kairo library versions aligned.

Use this BOM when you're adding one or a few Kairo libraries to an existing project
and want to avoid version conflicts between them.

If you're building a full Kairo application, consider using
[Kairo's full BOM](../bom-full/README.md) instead.

## Installation

```kotlin
// build.gradle.kts

dependencies {
  implementation(platform("software.airborne.kairo:bom:6.0.0"))

  // Now you can omit the version for any Kairo library.
  implementation("software.airborne.kairo:kairo-id")
  implementation("software.airborne.kairo:kairo-money")
}
```

## What's included

The BOM aligns versions for all Kairo modules.
It does not manage versions for external libraries.
For that, see [Kairo's full BOM](../bom-full/README.md).
