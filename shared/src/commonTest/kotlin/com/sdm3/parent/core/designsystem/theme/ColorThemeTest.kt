package com.sdm3.parent.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ColorThemeTest {

    @Test
    fun testLightModeColorsAreCorrectlyMapped() {
        val lightColors = productSchoolLightColors()
        
        // Brand Identity Check
        assertEquals(Color(0xFF001B3D), lightColors.primary, "Light primary must be Deep Navy")
        assertEquals(Color(0xFFD4AF37), lightColors.secondary, "Light secondary must be Academic Gold")
        assertEquals(Color(0xFF2D6CDF), lightColors.tertiary, "Light tertiary must be Modern Blue")
        
        // Background and surface
        assertEquals(Color(0xFFF7F8FA), lightColors.background, "Light background is Soft White")
        assertEquals(Color(0xFFFFFFFF), lightColors.surface, "Light surface is Pure White")
    }

    @Test
    fun testDarkModeColorsAreBrandSynchronized() {
        val darkColors = productSchoolDarkColors()
        
        // Dark primary must be a vibrant light navy/blue tint harmonic with primary
        assertEquals(Color(0xFF9CCAFF), darkColors.primary, "Dark primary must be the vibrant brand navy tint")
        // Dark secondary must be an accessible premium gold
        assertEquals(Color(0xFFE5C158), darkColors.secondary, "Dark secondary must be the accessible brand gold")
        
        // Dark background and surface must be derived from the brand Deep Navy
        assertEquals(Color(0xFF000B18), darkColors.background, "Dark background must be deep midnight navy")
        assertEquals(Color(0xFF001530), darkColors.surface, "Dark surface must be dark brand navy")
    }

    @Test
    fun testGlassSurfaceAlphaRanges() {
        val darkColors = productSchoolDarkColors()
        val lightColors = productSchoolLightColors()

        // Requirements require dark mode Glass_Surface alpha between 0.70 and 0.90
        val darkGlassAlpha = darkColors.liquidGlassSurface.alpha
        assertTrue(darkGlassAlpha in 0.70f..0.90f, "Dark mode liquidGlassSurface alpha ($darkGlassAlpha) must be between 0.70 and 0.90")

        // Light mode glass alpha
        val lightGlassAlpha = lightColors.liquidGlassSurface.alpha
        assertTrue(lightGlassAlpha in 0.70f..0.90f, "Light mode liquidGlassSurface alpha ($lightGlassAlpha) must be between 0.70 and 0.90")
    }
}
