@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package com.sdm3.parent.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.FontResource
import sdmuhammadiyah3samarinda.shared.generated.resources.*

private val interFonts: List<Pair<FontResource, FontWeight>> = listOf(
    Res.font.inter_regular to FontWeight.Normal,
    Res.font.inter_medium to FontWeight.Medium,
    Res.font.inter_semibold to FontWeight.SemiBold,
    Res.font.inter_bold to FontWeight.Bold,
    Res.font.inter_black to FontWeight.Black,
)

internal val interFontFamily: FontFamily
    @Composable get() = FontFamily(
        interFonts.map { (resource, weight) ->
            org.jetbrains.compose.resources.Font(
                resource = resource,
                weight = weight,
            )
        }
    )
