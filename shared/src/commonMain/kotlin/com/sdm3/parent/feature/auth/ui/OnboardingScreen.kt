package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        title = "Pantau Perkembangan Anak",
        subtitle = "Lihat nilai, kehadiran, dan rapor anak Anda kapan saja dengan mudah."
    ),
    OnboardingPage(
        title = "Bayar SPP Kapan Saja",
        subtitle = "Tidak perlu antre. Bayar SPP, DPP, dan biaya sekolah lainnya dengan mudah via aplikasi."
    ),
    OnboardingPage(
        title = "Unduh & Verifikasi Rapor",
        subtitle = "Rapor PDF resmi sekolah bisa diunduh kapan saja. Scan QR untuk verifikasi keaslian dokumen."
    )
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onComplete
            ) {
                Text(
                    text = "Lewati",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val data = onboardingPages[page]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .padding(Spacing.xl),
                    contentAlignment = Alignment.Center
                ) {
                    OnboardingVisualPremium(page = page)
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                Text(
                    text = data.title,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.lg),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(onboardingPages.size) { index ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 32.dp else 8.dp,
                    animationSpec = tween(durationMillis = 400)
                )
                val color = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
                Box(
                    modifier = Modifier
                        .padding(horizontal = Spacing.xxs)
                        .size(width = width, height = 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
        ) {
            val isLastPage = pagerState.currentPage == onboardingPages.size - 1
            Sdm3Button(
                text = if (isLastPage) "Mulai Sekarang" else "Lanjut",
                onClick = {
                    if (isLastPage) {
                        onComplete()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.AutoMirrored.Outlined.ArrowForward
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.xl),
            contentAlignment = Alignment.Center
        ) {
            if (pagerState.currentPage < onboardingPages.size - 1) {
                TextButton(onClick = onComplete) {
                    Text(
                        "Sudah punya akun? Masuk",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Spacer(Modifier.height(Spacing.xxl))
            }
        }
    }
}

@Composable
private fun OnboardingVisualPremium(page: Int) {
    val colorScheme = MaterialTheme.colorScheme
    when (page) {
        0 -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(160.dp),
                    shape = CardShape,
                    color = colorScheme.primaryContainer,
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Analytics,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = colorScheme.primary
                        )
                    }
                }
            }
        }
        1 -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(160.dp, 220.dp),
                    shape = CardShape,
                    color = colorScheme.secondaryContainer,
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = StatusSuccess,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }
        }
        2 -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(180.dp, 220.dp),
                    shape = CardShape,
                    color = colorScheme.surfaceVariant,
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(Modifier.padding(Spacing.lg)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = colorScheme.error.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.PictureAsPdf,
                                        contentDescription = null,
                                        tint = colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Box(modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(colorScheme.onSurface.copy(alpha = 0.1f)))
                        }
                        Spacer(Modifier.weight(1f))
                        Surface(
                            modifier = Modifier.size(72.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.QrCode2,
                                    contentDescription = null,
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
