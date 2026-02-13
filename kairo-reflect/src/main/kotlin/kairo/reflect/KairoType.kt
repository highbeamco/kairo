package kairo.reflect

import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.allSupertypes
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

/**
 * Unifies [Class], [KClass], [Type], and [KType] into a safer and richer wrapper.
 * Preserves full generic fidelity at runtime.
 *
 * Used by kairo-ktor, kairo-serialization, and application code
 * to preserve type information that JVM type erasure would otherwise lose.
 */
public data class KairoType<T>(
  public val kotlinType: KType,
) {
  public val javaType: Type
    get() = kotlinType.javaType

  @Suppress("UNCHECKED_CAST")
  public val kotlinClass: KClass<T & Any>
    get() = kotlinType.classifier as KClass<T & Any>

  public val javaClass: Class<T & Any>
    get() = kotlinClass.java

  public companion object {
    /**
     * Infers a [KairoType] at runtime from within a generic abstract class.
     *
     * @param T the type being resolved.
     * @param baseClass the generic parent class (e.g. `Repository::class`).
     * @param i the type argument index (0-based).
     * @param thisClass the concrete subclass whose type arguments are being resolved.
     */
    public fun <T> from(baseClass: KClass<*>, i: Int, thisClass: KClass<*>): KairoType<T> {
      val supertype = thisClass.allSupertypes.single { it.classifier == baseClass }
      val type = checkNotNull(supertype.arguments[i].type)
      return KairoType(type)
    }
  }
}

/** Captures a [KairoType] using Kotlin's reified generics. */
public inline fun <reified T> kairoType(): KairoType<T> =
  KairoType(typeOf<T>())
