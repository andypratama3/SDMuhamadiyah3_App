package com.sdm3.parent.feature.notifikasi.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.component.Sdm3EmptyState
import com.sdm3.parent.core.designsystem.component.Sdm3ErrorState
import com.sdm3.parent.core.designsystem.component.ErrorStateStyle
import com.sdm3.parent.core.designsystem.component.EmptyStateStyle
import com.sdm3.parent.core.designsystem.component.ScreenUiState
import com.sdm3.parent.core.designsystem.component.resolveScreenState
import com.sdm3.parent.core.designsystem.theme.*
import androidx.compose.ui.platform.LocalInspectionMode
import coil3.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import com.sdm3.parent.feature.notifikasi.DetailPengumumanViewModel
import com.sdm3.parent.platform.PlatformActions
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPengumumanScreen(
    announcementId: String,
    onBack: () -> Unit,
    viewModel: DetailPengumumanViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val vmUiState by if (isPreview) {
        remember { mutableStateOf(com.sdm3.parent.feature.notifikasi.DetailPengumumanUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val colorScheme = MaterialTheme.colorScheme
    val glassBorder = liquidGlassBorderColor()

    val errorMessage = vmUiState.errorMessage

    val screenState = remember(vmUiState.isLoading, vmUiState.isEmpty, errorMessage, isPreview) {
        if (isPreview) ScreenUiState.Success
        else resolveScreenState(vmUiState.isLoading, vmUiState.isEmpty, errorMessage)
    }

    if (!isPreview) {
        LaunchedEffect(announcementId) {
            viewModel.loadDetail(announcementId)
        }
    }

    ScreenScaffold(
        title = "Detail Informasi",
        subtitle = "PUBLIKASI RESMI",
        onBack = onBack,
        actions = {
            IconButton(
                onClick = {
                    val shareText = buildString {
                        appendLine(vmUiState.title)
                        appendLine()
                        append(vmUiState.content)
                    }
                    PlatformActions.shareText(shareText.trim(), vmUiState.title)
                }
            ) {
                Icon(Icons.Outlined.Share, contentDescription = "Bagikan", tint = colorScheme.primary)
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            ScreenGlowBackground(color = colorScheme.primaryContainer)

            when (screenState) {
                is ScreenUiState.Loading -> {
                    ShimmerDetailPengumuman()
                }
                is ScreenUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                        Sdm3EmptyState(
                            title = "Informasi Tidak Tersedia",
                            message = "Detail pengumuman yang Anda cari tidak ditemukan.",
                            style = EmptyStateStyle.Neutral
                        )
                    }
                }
                is ScreenUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                        Sdm3ErrorState(
                            title = "Gagal Memuat",
                            message = screenState.message,
                            style = ErrorStateStyle.Generic,
                            primaryAction = {
                                Sdm3Button(
                                    text = "Coba Lagi",
                                    onClick = { viewModel.refresh() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        )
                    }
                }
                is ScreenUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (vmUiState.imageUrl != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                            ) {
                                AsyncImage(
                                    model = vmUiState.imageUrl,
                                    contentDescription = "Gambar pengumuman",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(32.dp))
                                        .border(1.dp, glassBorder, RoundedCornerShape(32.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(32.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, overlayScrimColor(alpha = 0.75f))
                                            )
                                        )
                                )
                                Surface(
                                    modifier = Modifier
                                        .padding(Spacing.lg)
                                        .align(Alignment.BottomStart),
                                    shape = RoundedCornerShape(8.dp),
                                    color = colorScheme.secondary
                                ) {
                                    Text(
                                        " INFORMASI ",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colorScheme.onSecondary,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xs)
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.alpha(0.6f)
                            ) {
                                Icon(Icons.Outlined.Event, contentDescription = "Tanggal", tint = colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(Spacing.xs))
                                Text(vmUiState.date, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = colorScheme.primary)

                                Spacer(Modifier.width(Spacing.xl))

                                Icon(Icons.Outlined.Person, contentDescription = "Penulis", tint = colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(Spacing.xs))
                                Text(vmUiState.author, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                            }

                            Spacer(modifier = Modifier.height(Spacing.xl))

                            Text(
                                text = vmUiState.title,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary,
                                lineHeight = 36.sp,
                                letterSpacing = (-0.5).sp
                            )

                            Spacer(modifier = Modifier.height(Spacing.xxl))

                            Text(
                                text = vmUiState.content,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colorScheme.primary,
                                lineHeight = 28.sp
                            )

                            if (vmUiState.attachments.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(Spacing.xxxxl))

                                SectionHeader(title = "Lampiran Digital", modifier = Modifier.padding(bottom = Spacing.sm))
                                vmUiState.attachments.forEach { attachment ->
                                    Sdm3Card(
                                        padding = Spacing.md,
                                        modifier = Modifier.clickable {
                                            PlatformActions.openUrl(attachment.url)
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                modifier = Modifier.size(44.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                color = colorScheme.primary.copy(alpha = 0.05f)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Outlined.FileDownload, contentDescription = "Unduh lampiran", tint = colorScheme.primary, modifier = Modifier.size(26.dp))
                                                }
                                            }
                                            Spacer(Modifier.width(Spacing.md))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    attachment.name,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colorScheme.primary
                                                )
                                                if (attachment.size != null) {
                                                    Text(
                                                        attachment.size,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = ProductSchoolTheme.colors.onSurfaceFaint
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(Spacing.xs))
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.xxxl))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShimmerDetailPengumuman() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                .clip(RoundedCornerShape(32.dp))
                .shimmerEffect()
        )

        Column(modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(100.dp, 16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                )
                Spacer(Modifier.width(Spacing.xl))
                Box(
                    modifier = Modifier.size(120.dp, 16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            Box(
                modifier = Modifier.fillMaxWidth(0.9f).height(28.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Box(
                modifier = Modifier.fillMaxWidth(0.6f).height(28.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            repeat(6) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
            }
            Box(
                modifier = Modifier.fillMaxWidth(0.7f).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
            )

            Spacer(modifier = Modifier.height(Spacing.xxxxl))

            Box(
                modifier = Modifier.fillMaxWidth(0.4f).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
            )
            Spacer(modifier = Modifier.height(Spacing.sm))

            Sdm3Card(padding = Spacing.md) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect()
                    )
                    Spacer(Modifier.width(Spacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier.fillMaxWidth(0.5f).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                        )
                        Spacer(Modifier.height(Spacing.xs))
                        Box(
                            modifier = Modifier.fillMaxWidth(0.3f).height(12.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxxl))
        }
    }
}

@Preview
@Composable
private fun DetailPengumumanScreenPreview() {
    SDM3Theme {
        DetailPengumumanScreen(announcementId = "", onBack = {})
    }
}
