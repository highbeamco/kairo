# Kairo Image

Convenience wrapper around Java's built-in `ImageIO` for format conversion.

## Installation

Install `kairo-image`.

```kotlin
// build.gradle.kts

dependencies {
  implementation("software.airborne.kairo:kairo-image")
}
```

## Usage

### `convertImage()`

Converts an image from one format to another using Java's `ImageIO`.
Takes a `ByteArray` (the source image) and a format name, returns the converted `ByteArray`.

```kotlin
val jpeg: ByteArray = convertImage(png, "jpeg")
```

Supported format names depend on your JVM's `ImageIO` providers.
Common formats include `"jpeg"`, `"png"`, `"gif"`, and `"bmp"`.
