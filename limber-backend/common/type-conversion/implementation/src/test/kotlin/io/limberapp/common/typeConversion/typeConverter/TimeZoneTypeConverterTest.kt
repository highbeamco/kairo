package io.limberapp.common.typeConversion.typeConverter

import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

internal class TimeZoneTypeConverterTest {
  @Test
  fun isValid() {
    assertNull(TimeZoneTypeConverter.isValid(""))
  }

  @Test
  fun parseString() {
    assertFails { TimeZoneTypeConverter.parseString("") }

    assertEquals(ZoneOffset.UTC, TimeZoneTypeConverter.parseString("Z"))
    assertFails { TimeZoneTypeConverter.parseString("A") }

    listOf("", "GMT", "UTC", "UT").forEach { prefix ->
      assertEquals(ZoneOffset.ofHours(-7),
          TimeZoneTypeConverter.parseString("$prefix-7"))
      assertEquals(ZoneOffset.ofHours(11),
          TimeZoneTypeConverter.parseString("$prefix+11"))
      assertFails { TimeZoneTypeConverter.parseString("${prefix}11") }
      assertEquals(ZoneOffset.ofHoursMinutes(-3, -30),
          TimeZoneTypeConverter.parseString("$prefix-03:30"))
      assertFails { TimeZoneTypeConverter.parseString("$prefix-3:30") }
      assertEquals(ZoneOffset.ofHoursMinutesSeconds(12, 34, 56),
          TimeZoneTypeConverter.parseString("$prefix+123456"))
      assertFails { TimeZoneTypeConverter.parseString("${prefix}123456") }
    }

    assertEquals(ZoneOffset.UTC, TimeZoneTypeConverter.parseString("GMT"))
    assertEquals(ZoneOffset.UTC, TimeZoneTypeConverter.parseString("UTC"))
    assertEquals(ZoneOffset.UTC, TimeZoneTypeConverter.parseString("UT"))

    assertEquals(ZoneId.of("America/New_York"),
        TimeZoneTypeConverter.parseString("America/New_York"))
    assertEquals(ZoneId.of("America/Edmonton"),
        TimeZoneTypeConverter.parseString("America/Edmonton"))
    assertFails { TimeZoneTypeConverter.parseString("America/Calgary") }

    assertFails { TimeZoneTypeConverter.parseString("!@#$%^&*()") }
  }

  @Test
  fun writeString() {
    assertEquals("Z", TimeZoneTypeConverter.writeString(ZoneOffset.UTC))

    assertEquals("-07:00",
        TimeZoneTypeConverter.writeString(ZoneOffset.ofHours(-7)))
    assertEquals("-07:00",
        TimeZoneTypeConverter.writeString(ZoneOffset.ofHoursMinutes(-7, 0)))
    assertEquals("+11:00",
        TimeZoneTypeConverter.writeString(ZoneOffset.ofHours(11)))
    assertEquals("-03:30",
        TimeZoneTypeConverter.writeString(ZoneOffset.ofHoursMinutes(-3, -30)))
    assertEquals("-03:30",
        TimeZoneTypeConverter.writeString(ZoneOffset.ofHoursMinutesSeconds(-3, -30, 0)))
    assertEquals("+12:34:56",
        TimeZoneTypeConverter.writeString(ZoneOffset.ofHoursMinutesSeconds(12, 34, 56)))

    assertEquals("America/New_York",
        TimeZoneTypeConverter.writeString(ZoneId.of("America/New_York")))
    assertEquals("America/Edmonton",
        TimeZoneTypeConverter.writeString(ZoneId.of("America/Edmonton")))
  }
}