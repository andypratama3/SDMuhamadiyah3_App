package com.sdm3.parent.feature.guru.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.Sdm3Card
import com.sdm3.parent.core.designsystem.component.Sdm3EmptyState
import com.sdm3.parent.core.designsystem.component.Sdm3OutlinedButton
import com.sdm3.parent.core.designsystem.component.ScreenGlowBackground
import com.sdm3.parent.core.designsystem.component.ScreenScaffold
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.guru.AbsensiSayaViewModel
import com.sdm3.parent.platform.DeviceLocation
import com.sdm3.parent.platform.LocationPermissionState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AbsensiSayaScreen(
    onBack: () -> Unit,
    viewModel: AbsensiSayaViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.load()
        viewModel.refreshLocationAccess()
    }

    LaunchedEffect(state.successMessage) {
        val message = state.successMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.consumeSuccessMessage()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onScreenResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.onScreenDispose()
        }
    }

    ScreenScaffold(
        title = "Absensi Saya",
        subtitle = "ABSENSI MANDIRI",
        onBack = onBack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        val errorMessage = state.errorMessage

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            ScreenGlowBackground(color = colorScheme.primary)

            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.isEmployeeProfileMissing -> {
                    Sdm3EmptyState(
                        title = "Profil Pegawai Belum Tersedia",
                        message = errorMessage.orEmpty(),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                errorMessage != null && state.employeeName.isBlank() -> {
                    Sdm3EmptyState(
                        title = "Gagal Memuat",
                        message = errorMessage,
                        modifier = Modifier.align(Alignment.Center),
                        action = {
                            Sdm3Button(text = "Coba Lagi", onClick = { viewModel.load() })
                        },
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                    ) {
                        Sdm3Card(padding = Spacing.xl) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        state.employeeName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.2).sp
                                    )
                                    Text(
                                        text = listOfNotNull(state.employeeNip, state.date).joinToString(" • "),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ProductSchoolTheme.colors.onSurfaceMuted,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                state.attendance?.checkInStatus?.let { status ->
                                    CheckInStatusBadge(status)
                                }
                            }
                        }

                        LocationPermissionCard(
                            permission = state.locationPermission,
                            statusMessage = state.locationStatus,
                            liveLocation = state.liveLocation,
                            isRequesting = state.isRequestingPermission,
                            showSettingsShortcut = state.showLocationSettingsShortcut,
                            onRequestPermission = { viewModel.requestLocationAccess() },
                            onOpenAppSettings = { viewModel.openAppSettings() },
                            onOpenLocationSettings = { viewModel.openLocationSettings() },
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            AttendanceTimeCard(
                                title = "Check In",
                                time = formatTime(state.attendance?.checkInTime),
                                modifier = Modifier.weight(1f),
                            )
                            AttendanceTimeCard(
                                title = "Check Out",
                                time = formatTime(state.attendance?.checkOutTime),
                                modifier = Modifier.weight(1f),
                            )
                        }

                        errorMessage?.takeIf { state.employeeName.isNotBlank() }?.let { message ->
                            Text(
                                text = message,
                                color = colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        state.attendance?.locationName?.let { locationName ->
                            Text(
                                text = "Area: $locationName",
                                style = MaterialTheme.typography.labelSmall,
                                color = ProductSchoolTheme.colors.onSurfaceFaint,
                            )
                        }
                        state.attendance?.checkInDistance?.let { distance ->
                            Text(
                                text = "Jarak check-in: ${distance.toInt()} m dari pusat area",
                                style = MaterialTheme.typography.labelSmall,
                                color = ProductSchoolTheme.colors.onSurfaceFaint,
                            )
                        }

                        Sdm3Button(
                            text = if (state.attendance?.checkInTime != null) "Check In Selesai" else "Check In",
                            onClick = { viewModel.checkIn() },
                            enabled = state.canCheckIn &&
                                !state.isSubmitting &&
                                state.isLocationReady,
                            icon = Icons.AutoMirrored.Outlined.Login,
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = state.isSubmitting && state.canCheckIn,
                        )

                        Sdm3Button(
                            text = when {
                                state.attendance?.checkOutTime != null -> "Check Out Selesai"
                                state.attendance?.checkInTime == null -> "Check In Dulu"
                                else -> "Check Out"
                            },
                            onClick = { viewModel.checkOut() },
                            enabled = state.canCheckOut &&
                                !state.isSubmitting &&
                                state.isLocationReady,
                            icon = Icons.AutoMirrored.Outlined.Logout,
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = state.isSubmitting && state.canCheckOut,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckInStatusBadge(status: String) {
    val colorScheme = MaterialTheme.colorScheme
    val (label, container, content) = when (status) {
        "tepat_waktu" -> Triple("Tepat Waktu", colorScheme.primaryContainer, colorScheme.onPrimaryContainer)
        "terlambat" -> Triple("Terlambat", colorScheme.tertiaryContainer, colorScheme.onTertiaryContainer)
        "alpha" -> Triple("Alpha", colorScheme.errorContainer, colorScheme.onErrorContainer)
        else -> Triple(status.replace('_', ' '), colorScheme.surfaceVariant, colorScheme.onSurfaceVariant)
    }
    Surface(color = container, shape = MaterialTheme.shapes.small) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            style = MaterialTheme.typography.labelSmall,
            color = content,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun LocationPermissionCard(
    permission: LocationPermissionState,
    statusMessage: String,
    liveLocation: DeviceLocation?,
    isRequesting: Boolean,
    showSettingsShortcut: Boolean,
    onRequestPermission: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onOpenLocationSettings: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    Sdm3Card(padding = Spacing.lg) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.LocationOn, contentDescription = "Status lokasi", tint = colorScheme.primary)
            Spacer(modifier = Modifier.padding(Spacing.xs))
            Column(modifier = Modifier.weight(1f)) {
                Text("Izin Lokasi & GPS", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    statusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = ProductSchoolTheme.colors.onSurfaceMuted,
                )
                liveLocation?.let { loc ->
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "Lat ${formatCoord(loc.latitude)} • Lng ${formatCoord(loc.longitude)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ProductSchoolTheme.colors.onSurfaceFaint,
                    )
                }
            }
        }

        if (permission != LocationPermissionState.GRANTED) {
            Spacer(modifier = Modifier.height(Spacing.md))
            if (permission != LocationPermissionState.LOCATION_SERVICES_OFF) {
                Sdm3Button(
                    text = if (isRequesting) "Meminta Izin..." else "Izinkan Akses Lokasi",
                    onClick = onRequestPermission,
                    enabled = !isRequesting,
                    icon = Icons.Outlined.LocationOn,
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = isRequesting,
                )
            }
            if (showSettingsShortcut) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Sdm3OutlinedButton(
                    text = if (permission == LocationPermissionState.LOCATION_SERVICES_OFF) {
                        "Aktifkan GPS"
                    } else {
                        "Buka Pengaturan Aplikasi"
                    },
                    onClick = {
                        if (permission == LocationPermissionState.LOCATION_SERVICES_OFF) {
                            onOpenLocationSettings()
                        } else {
                            onOpenAppSettings()
                        }
                    },
                    icon = Icons.Outlined.Settings,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun AttendanceTimeCard(
    title: String,
    time: String,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    Sdm3Card(modifier = modifier, padding = Spacing.md) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = ProductSchoolTheme.colors.onSurfaceMuted)
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(time, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }
}

private fun formatTime(raw: String?): String {
    if (raw.isNullOrBlank()) return "--:--"
    return raw.take(5)
}

private fun formatCoord(value: Double): String {
    val str = value.toString()
    val parts = str.split(".")
    if (parts.size == 2) {
        val integerPart = parts[0]
        val decimalPart = parts[1].take(6).padEnd(6, '0')
        return "$integerPart.$decimalPart"
    }
    return str
}
