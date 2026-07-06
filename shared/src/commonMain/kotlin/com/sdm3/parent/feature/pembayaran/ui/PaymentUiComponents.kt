package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.Sdm3Card
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.core.util.formatRupiah
import com.sdm3.parent.core.util.formatTanggal
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import com.sdm3.parent.feature.pembayaran.*

enum class PaymentTab {
    Tagihan,
    Riwayat,
}

@Composable
fun Modifier.paymentHorizontalPadding(): Modifier = padding(horizontal = Spacing.xl)

@Composable
fun Modifier.paymentBottomSafePadding(extra: androidx.compose.ui.unit.Dp = Spacing.xxxl): Modifier =
    navigationBarsPadding().padding(bottom = extra)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreenScaffold(
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    Scaffold(
        modifier = modifier,
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Administrasi Keuangan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = (-0.5).sp,
                        )
                        Text(
                            text = "STATUS PEMBAYARAN SPP",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = colorScheme.primary.copy(alpha = 0.4f),
                            letterSpacing = 1.sp,
                        )
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = colorScheme.primary,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        content = content,
    )
}

@Composable
fun PaymentScreenBackground(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Canvas(modifier = modifier.fillMaxSize().alpha(0.2f)) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colorScheme.secondary.copy(alpha = 0.4f), Color.Transparent),
                center = Offset(size.width, size.height * 0.3f),
                radius = size.width,
            ),
        )
    }
}

@Composable
fun PaymentTabSelector(
    selectedTab: PaymentTab,
    onTabSelected: (PaymentTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val tabs = listOf(PaymentTab.Tagihan to "Tagihan", PaymentTab.Riwayat to "Riwayat")
    PrimaryTabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier.fillMaxWidth(),
        containerColor = Color.Transparent,
        contentColor = colorScheme.primary,
        divider = {},
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTabIndex = selectedTab.ordinal),
                color = colorScheme.secondary,
                height = 3.dp,
            )
        },
    ) {
        tabs.forEach { (tab, label) ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                    )
                },
                selectedContentColor = colorScheme.secondary,
                unselectedContentColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
fun PaymentHeroCard(
    activeFee: StudentFeeDto?,
    studentName: String,
    onBayarSekarang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val heroContent = heroContentColor()
    val statusSuccess = statusSuccessColor()
    val hasActive = activeFee != null
    val statusLabel = activeFee?.feeStatusLabel() ?: "LUNAS"
    val statusColor = when {
        !hasActive -> statusSuccess
        activeFee.isPaid() -> statusSuccess
        statusLabel == "TERLAMBAT" -> colorScheme.error
        else -> colorScheme.error
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                colorScheme.primary,
                                colorScheme.inversePrimary.copy(alpha = 0.5f),
                            ),
                        ),
                    )
                    .padding(Spacing.xl),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TAGIHAN AKTIF",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = heroContent.copy(alpha = 0.5f),
                        )
                        Spacer(modifier = Modifier.height(Spacing.xxs))
                        Text(
                            text = activeFee?.paymentTitleName
                                ?: if (hasActive) "Tagihan Aktif" else "Semua Tagihan Lunas",
                            style = MaterialTheme.typography.titleLarge,
                            color = heroContent,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (studentName.isNotBlank()) {
                            Spacer(modifier = Modifier.height(Spacing.xxs))
                            Text(
                                text = studentName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = heroContent.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = statusColor,
                    ) {
                        Text(
                            text = " $statusLabel ",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onPrimary,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 6.dp),
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(Spacing.xl)) {
                Text(
                    text = formatRupiah(activeFee?.amount ?: 0.0),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = heroContent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
                val dueLabel = activeFee?.dueDate?.takeIf { it.isNotBlank() }?.let {
                    "Jatuh tempo: ${formatTanggal(it)}"
                }.orEmpty()
                if (dueLabel.isNotEmpty()) {
                    Text(
                        text = dueLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = heroContent.copy(alpha = 0.6f),
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                Sdm3Button(
                    text = if (hasActive) "Bayar Sekarang" else "Tidak Ada Tagihan",
                    onClick = onBayarSekarang,
                    enabled = hasActive,
                    icon = Icons.Outlined.CreditCard,
                    containerColor = colorScheme.secondary,
                    contentColor = colorScheme.primary,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                )
            }
        }
    }
}

@Composable
fun PaymentProgressCard(
    progress: PaymentProgressInfo,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()

    Sdm3Card(modifier = modifier, padding = Spacing.lg) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Kelancaran Pembayaran",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${progress.percent}%",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = statusSuccess,
                )
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            LinearProgressIndicator(
                progress = { progress.ratio },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = statusSuccess,
                trackColor = statusSuccess.copy(alpha = 0.1f),
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "${progress.paid} dari ${progress.total} tagihan telah diselesaikan.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
fun PaymentYearHeader(
    year: Int,
    itemCount: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "yearChevron",
    )
    val yearLabel = if (year > 0) year.toString() else "Lainnya"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onToggle)
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = yearLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
            )
            Text(
                text = "$itemCount item",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = if (expanded) "Tutup" else "Buka",
            tint = colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.rotate(rotation),
        )
    }
}

@Composable
fun PaymentMonthHeader(
    monthName: String,
    itemCount: Int,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.xs, bottom = Spacing.xxs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = monthName.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = colorScheme.primary.copy(alpha = 0.5f),
        )
        Text(
            text = "$itemCount",
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        )
    }
}

@Composable
fun FeeItemCard(
    fee: StudentFeeDto,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()
    val statusWarning = statusWarningColor()
    val isPaid = fee.isPaid()
    val statusLabel = fee.feeStatusLabel()
    val statusColor = when {
        isPaid -> statusSuccess
        statusLabel == "TERLAMBAT" -> colorScheme.error
        else -> statusWarning
    }
    val subtitle = fee.dueDate?.takeIf { it.isNotBlank() }?.let {
        "Jatuh tempo ${formatTanggal(it)}"
    } ?: "Status $statusLabel"

    Sdm3Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        padding = 12.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = colorScheme.primary.copy(alpha = 0.03f),
                border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.05f)),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPaid) Icons.Outlined.Verified else Icons.Outlined.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (isPaid) statusSuccess else colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fee.paymentTitleName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    letterSpacing = (-0.2).sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Surface(
                color = statusColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.12f)),
            ) {
                Text(
                    text = formatRupiah(fee.amount),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = statusColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
            if (onClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
fun PaymentHistoryCard(
    payment: PaymentDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()
    val statusWarning = statusWarningColor()
    val statusLower = payment.status.lowercase()
    val isPaid = statusLower in PAID_PAYMENT_STATUSES
    val isFailed = statusLower in FAILED_PAYMENT_STATUSES
    val statusLabel = payment.paymentStatusLabel()
    val statusColor = when {
        isPaid -> statusSuccess
        isFailed -> colorScheme.error
        else -> statusWarning
    }
    val title = payment.paymentTitle?.name ?: payment.orderId
    val dateRaw = payment.paidAt ?: payment.createdAt

    Sdm3Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        padding = 12.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = colorScheme.primary.copy(alpha = 0.03f),
                border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.05f)),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPaid) Icons.Outlined.Verified else Icons.Outlined.History,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (isPaid) statusSuccess else colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    letterSpacing = (-0.2).sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Status $statusLabel · ${formatTanggal(dateRaw)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Surface(
                color = statusColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.12f)),
            ) {
                Text(
                    text = formatRupiah(payment.grossAmount ?: 0.0),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = statusColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
fun PaymentGroupedYearSection(
    year: Int,
    itemCount: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
    ) {
        PaymentYearHeader(
            year = year,
            itemCount = itemCount,
            expanded = expanded,
            onToggle = onToggle,
        )
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = content,
            )
        }
    }
}

@Preview
@Composable
private fun PaymentUiComponentsPreview() {
    SDM3Theme {
        Column(
            modifier = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            PaymentTabSelector(
                selectedTab = PaymentTab.Tagihan,
                onTabSelected = {},
            )
            PaymentProgressCard(
                progress = PaymentProgressInfo(total = 12, paid = 8, percent = 67),
            )
        }
    }
}
