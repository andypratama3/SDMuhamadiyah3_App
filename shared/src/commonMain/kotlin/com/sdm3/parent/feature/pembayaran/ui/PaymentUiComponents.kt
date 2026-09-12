package com.sdm3.parent.feature.pembayaran.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.Sdm3Card
import com.sdm3.parent.core.designsystem.component.Sdm3IconBadge
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
fun Modifier.paymentBottomSafePadding(): Modifier =
    navigationBarsPadding().padding(bottom = Spacing.bottomNavSafeArea)

@Composable
fun PaymentTabSelector(
    selectedTab: PaymentTab,
    onTabSelected: (PaymentTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val tabs = listOf(PaymentTab.Tagihan to "Tagihan", PaymentTab.Riwayat to "Riwayat")
    SecondaryTabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier.fillMaxWidth(),
        containerColor = Color.Transparent,
        divider = {},
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
                selectedContentColor = colorScheme.primary,
                unselectedContentColor = ProductSchoolTheme.colors.onSurfaceMuted,
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
    val statusDanger = statusDangerColor()
    val statusWarning = statusWarningColor()
    val hasActive = activeFee != null
    val statusLabel = activeFee?.feeStatusLabel() ?: "LUNAS"
    val statusColor = when {
        !hasActive -> statusSuccess
        activeFee.isPaid() -> statusSuccess
        statusLabel == "TERLAMBAT" -> statusDanger
        else -> statusWarning
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                colorScheme.inversePrimary.copy(alpha = 0.4f),
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
                            letterSpacing = 1.2.sp,
                            color = heroContent.copy(alpha = 0.55f),
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = activeFee?.paymentTitleName
                                ?: if (hasActive) "Tagihan Aktif" else "Semua Tagihan Lunas",
                            style = MaterialTheme.typography.titleLarge,
                            color = heroContent,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 28.sp
                        )
                        if (studentName.isNotBlank()) {
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Text(
                                text = studentName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = heroContent.copy(alpha = 0.75f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = statusColor,
                    ) {
                        Text(
                            text = " $statusLabel ",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onPrimary,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = Spacing.md, vertical = 7.dp),
                            letterSpacing = 0.3.sp
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
                    letterSpacing = (-0.5).sp
                )
                val dueLabel = activeFee?.dueDate?.takeIf { it.isNotBlank() }?.let {
                    "Jatuh tempo: ${formatTanggal(it)}"
                }.orEmpty()
                if (dueLabel.isNotEmpty()) {
                    Text(
                        text = dueLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = heroContent.copy(alpha = 0.65f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                Sdm3Button(
                    text = if (hasActive) "Bayar Sekarang" else "Tidak Ada Tagihan",
                    onClick = onBayarSekarang,
                    enabled = hasActive,
                    icon = Icons.Outlined.CreditCard,
                    containerColor = colorScheme.secondary,
                    contentColor = colorScheme.onSecondary,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
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

    Sdm3Card(modifier = modifier, padding = Spacing.xl) {
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
                    letterSpacing = (-0.2).sp
                )
                Text(
                    text = "${progress.percent}%",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = statusSuccess,
                    letterSpacing = (-0.3).sp
                )
            }
            Spacer(modifier = Modifier.height(Spacing.md))
            LinearProgressIndicator(
                progress = { progress.ratio },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                color = statusSuccess,
                trackColor = statusSuccess.copy(alpha = 0.12f),
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(
                text = "${progress.paid} dari ${progress.total} tagihan telah diselesaikan.",
                style = MaterialTheme.typography.bodyMedium,
                color = ProductSchoolTheme.colors.onSurfaceMuted,
                fontWeight = FontWeight.Medium
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
                color = ProductSchoolTheme.colors.onSurfaceFaint,
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = if (expanded) "Tutup" else "Buka",
            tint = ProductSchoolTheme.colors.onSurfaceMuted,
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
            .padding(top = Spacing.xs, bottom = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = monthName.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = ProductSchoolTheme.colors.onSurfaceMuted,
        )
        Text(
            text = "$itemCount",
            style = MaterialTheme.typography.labelSmall,
            color = ProductSchoolTheme.colors.onSurfaceFaint,
        )
    }
}

/**
 * Shared payment card layout — eliminates the identical Row(Sm3Card, icon, column, amount, chevron)
 * pattern duplicated in FeeItemCard and PaymentHistoryCard.
 */
@Composable
private fun PaymentCardLayout(
    title: String,
    subtitle: String,
    amount: String,
    statusColor: Color,
    icon: ImageVector,
    iconTint: Color,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
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
            Sdm3IconBadge(
                icon = icon,
                size = 52.dp,
                iconSize = 24.dp,
                iconTint = iconTint,
            )
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
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = ProductSchoolTheme.colors.onSurfaceMuted,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Surface(
                color = statusColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.5.dp, statusColor.copy(alpha = 0.2f))
            ) {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = statusColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
            if (onClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = ProductSchoolTheme.colors.onSurfaceMuted,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
fun FeeItemCard(
    fee: StudentFeeDto,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val statusSuccess = statusSuccessColor()
    val statusWarning = statusWarningColor()
    val statusDanger = statusDangerColor()
    val isPaid = fee.isPaid()
    val statusLabel = fee.feeStatusLabel()
    val statusColor = when {
        isPaid -> statusSuccess
        statusLabel == "TERLAMBAT" -> statusDanger
        else -> statusWarning
    }
    val subtitle = fee.dueDate?.takeIf { it.isNotBlank() }?.let {
        "Jatuh tempo ${formatTanggal(it)}"
    } ?: "Status $statusLabel"

    PaymentCardLayout(
        title = fee.paymentTitleName,
        subtitle = subtitle,
        amount = formatRupiah(fee.amount),
        statusColor = statusColor,
        icon = if (isPaid) Icons.Outlined.Verified else Icons.Outlined.Receipt,
        iconTint = if (isPaid) statusSuccess else MaterialTheme.colorScheme.primary,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun PaymentHistoryCard(
    payment: PaymentDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusSuccess = statusSuccessColor()
    val statusWarning = statusWarningColor()
    val statusDanger = statusDangerColor()
    val statusLower = payment.status.lowercase()
    val isPaid = statusLower in PAID_PAYMENT_STATUSES
    val isFailed = statusLower in FAILED_PAYMENT_STATUSES
    val statusLabel = payment.paymentStatusLabel()
    val statusColor = when {
        isPaid -> statusSuccess
        isFailed -> statusDanger
        else -> statusWarning
    }
    val title = payment.paymentTitle?.name ?: payment.orderId
    val dateRaw = payment.paidAt ?: payment.createdAt

    PaymentCardLayout(
        title = title,
        subtitle = "Status $statusLabel · ${formatTanggal(dateRaw)}",
        amount = formatRupiah(payment.grossAmount ?: 0.0),
        statusColor = statusColor,
        icon = if (isPaid) Icons.Outlined.Verified else Icons.Outlined.History,
        iconTint = if (isPaid) statusSuccess else MaterialTheme.colorScheme.primary,
        onClick = onClick,
        modifier = modifier,
    )
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
