package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.getPlatformName
import kotlinx.coroutines.launch

private val PremiumEasing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val label: String
)

private val onboardingPages = listOf(
    OnboardingPage(
        label = "AKADEMIK",
        title = "Pantau Perkembangan Anak Setiap Hari",
        subtitle = "Akses nilai sumatif, kehadiran, dan rapor digital dalam satu platform terpadu."
    ),
    OnboardingPage(
        label = "ADMINISTRASI",
        title = "Kelola Pembiayaan Tanpa Antre",
        subtitle = "Lakukan pembayaran SPP dan biaya pendidikan lainnya dengan aman melalui sistem cashless."
    ),
    OnboardingPage(
        label = "KOMUNIKASI",
        title = "Informasi Sekolah Real-time",
        subtitle = "Terima notifikasi pengumuman penting dan jadwal kegiatan sekolah langsung di ponsel Anda."
    )
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    initialPage: Int = 0
) {
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val isPreview = LocalInspectionMode.current

    Scaffold(
        containerColor = colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Modern Atmospheric Background
            AtmosphericGlow(
                alignment = Alignment.TopEnd,
                color = colorScheme.primary
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .safeDrawingPadding()
            ) {
                // Header Skip Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(
                        onClick = onComplete,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "LEWATI",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.6.sp,
                            color = ProductSchoolTheme.colors.onSurfaceMuted
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Spacing.xxl, vertical = Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        OnboardingVisual(page = page)

                        Spacer(modifier = Modifier.height(Spacing.xxxxl))

                        // Category Badge
                        Sdm3CategoryBadge(text = onboardingPages[page].label)

                        Spacer(modifier = Modifier.height(Spacing.xl))

                        Text(
                            text = onboardingPages[page].title,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.4).sp,
                            lineHeight = 34.sp,
                            maxLines = 3,
                        )

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        Text(
                            text = onboardingPages[page].subtitle,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = ProductSchoolTheme.colors.onSurfaceMuted,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Navigation Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = Spacing.xl)
                        .padding(bottom = Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Modern Pager Indicators
                    Row(
                        modifier = Modifier.height(48.dp),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(onboardingPages.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            val width by animateDpAsState(
                                targetValue = if (isSelected) 32.dp else 8.dp,
                                animationSpec = tween(400, easing = PremiumEasing)
                            )
                            val alpha by animateFloatAsState(
                                targetValue = if (isSelected) 1f else 0.3f,
                                animationSpec = tween(400)
                            )

                            Box(
                                modifier = Modifier
                                    .size(width = width, height = 6.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.primary.copy(alpha = alpha))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.xl))

                    val isLastPage = pagerState.currentPage == onboardingPages.size - 1

                    Sdm3Button(
                        text = if (isLastPage) "Mulai Sekarang" else "Lanjutkan",
                        onClick = {
                            if (!isPreview && getPlatformName() != "iOS") {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            if (isLastPage) {
                                onComplete()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    AnimatedVisibility(
                        visible = !isLastPage,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        TextButton(onClick = onComplete) {
                            Text(
                                text = "Sudah punya akun? Masuk Saja",
                                style = MaterialTheme.typography.labelLarge,
                                color = ProductSchoolTheme.colors.onSurfaceMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingVisual(page: Int) {
    val colorScheme = MaterialTheme.colorScheme
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Decorative Bloom
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    Brush.radialGradient(
                        listOf(colorScheme.primary.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )

        // Floating Glass Card
        Sdm3GlassCard(
            modifier = Modifier
                .size(170.dp, 200.dp)
                .graphicsLayer { translationY = floatOffset },
            padding = 0.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(88.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when (page) {
                                0 -> Icons.Outlined.Analytics
                                1 -> Icons.Outlined.Payments
                                else -> Icons.Outlined.Forum
                            },
                            contentDescription = when (page) {
                                0 -> "Akademik"
                                1 -> "Pembayaran"
                                else -> "Komunikasi"
                            },
                            modifier = Modifier.size(44.dp),
                            tint = colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.xl))
                // Faux Data Lines
                Box(modifier = Modifier.width(100.dp).height(10.dp).clip(RoundedCornerShape(5.dp)).background(colorScheme.primary.copy(alpha = 0.12f)))
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.width(70.dp).height(8.dp).clip(RoundedCornerShape(4.dp)).background(colorScheme.primary.copy(alpha = 0.08f)))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    SDM3Theme {
        OnboardingScreen(onComplete = {}, initialPage = 0)
    }
}
