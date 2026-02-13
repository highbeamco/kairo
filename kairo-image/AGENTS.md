# kairo-image

Thin wrapper around Java's ImageIO for image format conversion.

## Key files
- `src/main/kotlin/kairo/image/convertImage.kt` -- `convertImage(ByteArray, String): ByteArray` function

## Patterns and conventions
- `formatName` must be a name recognized by Java's ImageIO (e.g. "jpeg", "png", "gif", "bmp")
- Available formats depend on the JVM's installed ImageIO plugins
- The default JDK ships with JPEG, PNG, GIF, BMP, and WBMP

## Foot-guns
- No validation is done on the input image; invalid image data will throw at `ImageIO.read()`
- The function loads the entire image into memory as a `BufferedImage`
