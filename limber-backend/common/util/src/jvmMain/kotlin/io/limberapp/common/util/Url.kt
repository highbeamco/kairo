@file:JvmName("UrlJvmKt")

package io.limberapp.common.util

import java.net.URLEncoder

actual fun enc(value: Any): String = URLEncoder.encode(value.toString(), "UTF-8")