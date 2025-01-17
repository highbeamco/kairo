@file:Suppress("ForbiddenMethodCall", "UnnecessaryLet")

package kairo.dependencyInjection

import com.google.inject.Injector
import com.google.inject.Key
import kotlin.reflect.KClass

public inline fun <reified T : Any> Injector.getInstance(): T =
  getInstance(Key.get(type<T>()))

public fun <T : Any> Injector.getInstanceByClass(kClass: KClass<T>): T =
  getInstance(Key.get(kClass.java))

public inline fun <reified T : Any> Injector.getInstanceOptional(): T? =
  getExistingBinding(Key.get(type<T>()))?.let { it.provider.get() }

public fun <T : Any> Injector.getInstanceOptionalByClass(kClass: KClass<T>): T? =
  getExistingBinding(Key.get(kClass.java))?.let { it.provider.get() }
