package com.sdm3.parent.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Teks/ikon di atas permukaan primary (hero card, header gelap). */
@Composable
fun heroContentColor(): Color = MaterialTheme.colorScheme.onPrimary

/** Isian glassmorphism (input, chip transparan). */
@Composable
fun glassSurfaceColor(): Color = ProductSchoolTheme.colors.surfaceGlass

/** Garis tepi glassmorphism. */
@Composable
fun glassBorderColor(): Color = ProductSchoolTheme.colors.glassOutline

@Composable
fun statusSuccessColor(): Color = ProductSchoolTheme.colors.success

@Composable
fun statusWarningColor(): Color = ProductSchoolTheme.colors.warning

@Composable
fun statusDangerColor(): Color = ProductSchoolTheme.colors.danger

@Composable
fun statusInfoColor(): Color = ProductSchoolTheme.colors.info
