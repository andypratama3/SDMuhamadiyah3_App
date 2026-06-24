package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val subtitle: String
)

private val onboardingPages = listOf(
    OnboardingPage(
        title = "Pantau Perkembangan Anak Setiap Hari",
        subtitle = "Lihat nilai, kehadiran, dan rapor anak Anda kapan saja dengan mudah."
    ),
    OnboardingPage(
        title = "Bayar SPP Kapan Saja",
        subtitle = "Tidak perlu antre. Bayar SPP, DPP, dan biaya sekolah lainnya dengan mudah via aplikasi."
    ),
    OnboardingPage(
        title = "Dapatkan Pengumuman Secara Real-time",
        subtitle = "Terima notifikasi pengumuman sekolah langsung di ponsel Anda."
    )
)

@OptIn(ExperimentalAnimationApi::class)
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

    if (!isPreview) {
        LaunchedEffect(pagerState.currentPage) {
            if (pagerState.currentPage > 0) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header skip button (Opsional, memberikan alternatif navigasi cepat)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = onComplete) {
                    Text(
                        text = "Lewati",
                        style = MaterialTheme.typography.labelLarge,
                        color = colorScheme.onSurfaceVariant
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
                        .padding(horizontal = Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f),
                        contentAlignment = Alignment.Center
                    ) {
                        OnboardingVisual(page = page)
                    }

                    Spacer(modifier = Modifier.height(Spacing.xl))

                    Text(
                        text = onboardingPages[page].title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Text(
                        text = onboardingPages[page].subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 3
                    )
                }
            }

            // Bottom Section (Indicators & Buttons)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pager Indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(onboardingPages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 32.dp else 8.dp,
                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                            label = "indicatorWidth"
                        )
                        val color by animateColorAsState(
                            targetValue = if (isSelected) colorScheme.primary else colorScheme.outlineVariant,
                            animationSpec = tween(durationMillis = 300),
                            label = "indicatorColor"
                        )

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = width, height = 8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                val isLastPage = pagerState.currentPage == onboardingPages.size - 1

                Sdm3Button(
                    text = "", // Dikosongkan karena kita menggunakan isi kustom di bawah
                    onClick = {
                        if (isLastPage) {
                            onComplete()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    AnimatedContent(
                        targetState = isLastPage,
                        transitionSpec = {
                            if (targetState) {
                                slideInVertically { height -> height } + fadeIn() togetherWith
                                        slideOutVertically { height -> -height } + fadeOut()
                            } else {
                                slideInVertically { height -> -height } + fadeIn() togetherWith
                                        slideOutVertically { height -> height } + fadeOut()
                            }.using(SizeTransform(clip = false))
                        },
                        label = "buttonTextAnimation"
                    ) { lastPage ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (lastPage) "Mulai Sekarang" else "Lanjutkan",
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Fixed height box agar layout tidak melompat saat teks login hilang
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(top = Spacing.sm),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedVisibility(
                        visible = !isLastPage,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        TextButton(onClick = onComplete) {
                            Text(
                                text = "Sudah punya akun? Masuk",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
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

    // Animasi melayang (floating effect)
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Latar belakang dekoratif statis
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(
                    when (page) {
                        0 -> colorScheme.primaryContainer.copy(alpha = 0.5f)
                        1 -> colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        else -> colorScheme.surfaceVariant
                    }
                )
        )

        // Konten yang melayang
        Box(
            modifier = Modifier.graphicsLayer { translationY = floatOffset }
        ) {
            when (page) {
                0 -> {
                    Card(
                        modifier = Modifier.size(160.dp, 180.dp),
                        shape = RoundedCornerShape(Spacing.xl),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(Spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.School,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))
                            Box(modifier = Modifier.width(80.dp).height(12.dp).clip(CircleShape).background(colorScheme.primaryContainer))
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Box(modifier = Modifier.width(50.dp).height(8.dp).clip(CircleShape).background(colorScheme.outlineVariant))
                        }
                    }
                }
                1 -> {
                    Card(
                        modifier = Modifier.size(160.dp, 180.dp),
                        shape = RoundedCornerShape(Spacing.xl),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(Spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountBalanceWallet,
                                contentDescription = null,
                                tint = colorScheme.secondary,
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))
                            Box(modifier = Modifier.width(60.dp).height(12.dp).clip(CircleShape).background(colorScheme.secondaryContainer))
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Box(modifier = Modifier.width(90.dp).height(8.dp).clip(CircleShape).background(colorScheme.outlineVariant))
                        }
                    }
                }
                2 -> {
                    Card(
                        modifier = Modifier.size(160.dp, 180.dp),
                        shape = RoundedCornerShape(Spacing.xl),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(Spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NotificationsActive,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))
                            Box(modifier = Modifier.width(100.dp).height(12.dp).clip(CircleShape).background(colorScheme.primaryContainer))
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Box(modifier = Modifier.width(70.dp).height(8.dp).clip(CircleShape).background(colorScheme.outlineVariant))
                        }
                    }
                }
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