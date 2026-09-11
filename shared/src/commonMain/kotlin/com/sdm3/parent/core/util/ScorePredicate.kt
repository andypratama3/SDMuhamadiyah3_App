package com.sdm3.parent.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sdm3.parent.core.designsystem.theme.statusDangerColor
import com.sdm3.parent.core.designsystem.theme.statusInfoColor
import com.sdm3.parent.core.designsystem.theme.statusSuccessColor
import com.sdm3.parent.core.designsystem.theme.statusWarningColor

/**
 * Predikat nilai standar SD (Kurikulum Merdeka) — dipakai seragam di seluruh
 * layar nilai/rapor agar 1 skor selalu punya 1 warna & 1 label.
 *
 *  >= 90  -> "SANGAT BAIK"  (success)
 *  >= 80  -> "BAIK"         (info)
 *  >= 70  -> "CUKUP"        (warning)
 *  sisanya -> "PERLU BIMBINGAN" (danger)
 */
@Composable
fun predicateForScore(score: Number): Pair<String, Color> {
    val value = score.toFloat()
    return when {
        value >= 90f -> "SANGAT BAIK" to statusSuccessColor()
        value >= 80f -> "BAIK" to statusInfoColor()
        value >= 70f -> "CUKUP" to statusWarningColor()
        else -> "PERLU BIMBINGAN" to statusDangerColor()
    }
}

/** Warna untuk skor/nilai (tanpa label), selaras dengan [predicateForScore]. */
@Composable
fun scoreColor(score: Number): Color = predicateForScore(score).second