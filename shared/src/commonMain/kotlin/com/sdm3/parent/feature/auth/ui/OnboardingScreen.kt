package com.sdm3.parent.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.Spacing
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val subtitle: String
)

private val onboardingPages = listOf(
    OnboardingPage(
        title = "Pantau Perkembangan Anak",
        subtitle = "Lihat nilai, kehadiran, dan rapor anak Anda kapan saja"
    ),
    OnboardingPage(
        title = "Bayar SPP Lebih Mudah",
        subtitle = "Pembayaran digital via transfer bank atau QRIS, tanpa antri"
    ),
    OnboardingPage(
        title = "Selalu Up-to-Date",
        subtitle = "Dapatkan pengumuman dan notifikasi langsung dari sekolah"
    )
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onComplete) {
                Text("Skip")
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
                    .padding(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = OnSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(onboardingPages.size) { index ->
                val color = if (pagerState.currentPage == index)
                    Primary else OnSurfaceVariant
                androidx.compose.foundation.Canvas(modifier = Modifier.padding(4.dp)) {
                    drawCircle(color = color, radius = 6.dp.toPx())
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        Sdm3Button(
            text = if (pagerState.currentPage == onboardingPages.size - 1) "Mulai Sekarang" else "Lanjut",
            onClick = {
                if (pagerState.currentPage == onboardingPages.size - 1) {
                    onComplete()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier.padding(Spacing.md)
        )

        Spacer(modifier = Modifier.height(Spacing.sm))
    }
}
