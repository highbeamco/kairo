package io.limberapp.web.util

import kotlinx.css.*

@Suppress("UnusedPrivateMember")
internal object Theme {
  object Color {
    private val jet = Color("#303030")
    private val jetLighter20 = Color("#636363")

    private val babyPowder = Color("#FCFCFC")
    private val babyPowderDarker05 = Color("#EFEFEF")
    private val babyPowderDarker10 = Color("#E3E3E3")
    private val babyPowderDarker20 = Color("#C9C9C9")

    // TODO (ENG-34): Clean css'ing
    // Color scheme for gray scale based on Github: https://primer.style/css/support/color-system
    private val grey = Color("#6A737D")
    private val grey100 = Color("#F6F8FA")
    private val grey200 = Color("#E1E4E8")
    private val grey300 = Color("#D1D5DA")

    private val nickel = Color("#707070")
    private val nickelDarker10 = Color("#575757")
    private val nickelDarker20 = Color("#3D3D3D")

    private val copper = Color("#CC8C70")

    private val blue = Color("#1078D8")
    private val blueDarker10 = Color("#005FBF")
    private val blueDarker50 = Color("#000059")

    private val red = Color("#C81818")
    private val redDarker10 = Color("#AF0000")

    object Button {
      object Primary {
        val backgroundDefault = blue
        val backgroundActive = blueDarker10
        val backgroundDisabled = blueDarker50
      }

      object Secondary {
        val backgroundDefault = nickel
        val backgroundActive = nickelDarker10
        val backgroundDisabled = nickelDarker20
      }

      object Red {
        val backgroundDefault = red
        val backgroundActive = redDarker10
        val backgroundDisabled = nickelDarker20
      }
    }

    object Link {
      val default = blue
      val active = blueDarker10
      val disabled = blueDarker50
    }

    val smallActiveIndicator = copper

    object Text {
      val dark = jet
      val light = grey100
      val link = blue
      val red = Color.red
    }

    object Background {
      val dark = jet
      val light = grey100
      val lightActive = grey200
      val lightDisabled = grey300
      val link = blue
      val white = kotlinx.css.Color.white
    }

    object Border {
      val dark = jetLighter20
      val light = grey300
    }
  }

  object ZIndex {
    const val subnav = 1
    const val modalFader = 2
    const val modalModal = 3
  }

  object Sizing {
    val borderRadius = 4.px
  }
}
