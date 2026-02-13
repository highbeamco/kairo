package kairo.image

import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Convenience wrapper for Java's built-in image conversion.
 * [formatName] must be a name recognized by [ImageIO], such as "jpeg", "png", "gif", or "bmp".
 * Available formats depend on the JVM's installed ImageIO plugins.
 */
public fun convertImage(image: ByteArray, formatName: String): ByteArray {
  ByteArrayOutputStream().use { outputStream ->
    val bufferedImage = image.inputStream().use { ImageIO.read(it) }
    ImageIO.write(bufferedImage, formatName, outputStream)
    return outputStream.toByteArray()
  }
}
