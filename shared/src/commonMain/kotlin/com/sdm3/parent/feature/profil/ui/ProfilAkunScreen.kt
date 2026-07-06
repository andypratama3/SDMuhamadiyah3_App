package com.sdm3.parent.feature.profil.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import sdmuhammadiyah3samarinda.shared.generated.resources.Res
import sdmuhammadiyah3samarinda.shared.generated.resources.teacher_absensi_saya_menu
import sdmuhammadiyah3samarinda.shared.generated.resources.teacher_panel_menu
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.component.Sdm3EmptyState
import com.sdm3.parent.core.designsystem.component.Sdm3ErrorState
import com.sdm3.parent.core.designsystem.component.ErrorStateStyle
import com.sdm3.parent.core.designsystem.component.EmptyStateStyle
import com.sdm3.parent.core.designsystem.theme.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import coil3.compose.AsyncImage
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.feature.auth.ui.PilihAnakBottomSheet
import com.sdm3.parent.feature.profil.ProfilAkunUiState
import com.sdm3.parent.feature.profil.ProfilAkunViewModel
import com.sdm3.parent.core.AppBranding
import com.sdm3.parent.platform.PlatformActions
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch

sealed class ProfilUiState {
    data object Loading : ProfilUiState()
    data object Empty : ProfilUiState()
    data class Error(val message: String) : ProfilUiState()
    data object Success : ProfilUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilAkunScreen(
    onNotifikasiSetting: () -> Unit,
    onAccountDeletion: () -> Unit,
    onLogout: () -> Unit,
    onOpenTeacherPanel: (() -> Unit)? = null,
    onOpenAbsensiSaya: (() -> Unit)? = null,
    selectedStudentId: String = "",
    onSwitchStudent: (String) -> Unit = {},
    viewModel: ProfilAkunViewModel = koinViewModel()
) {
    val isPreview = LocalInspectionMode.current
    val secureTokenManager: com.sdm3.parent.core.security.SecureTokenManager? = if (isPreview) null else koinInject()
    val hasTeacherAccess = secureTokenManager?.getRoleContext()?.hasTeacherAccess == true
    val uiState by if (isPreview) {
        remember { mutableStateOf(ProfilAkunUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showStudentSheet by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    if (!isPreview) {
        LaunchedEffect(viewModel) {
            viewModel.logoutCompletedFlow.collect {
                onLogout()
            }
        }
    }

    val errorMessage = uiState.errorMessage

    val screenState = remember(uiState.isLoading, uiState.isEmpty, errorMessage, isPreview) {
        if (isPreview) ProfilUiState.Success
        else when {
            uiState.isLoading && uiState.isEmpty -> ProfilUiState.Loading
            errorMessage != null -> ProfilUiState.Error(errorMessage)
            !uiState.isLoading && uiState.isEmpty -> ProfilUiState.Empty
            else -> ProfilUiState.Success
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showInfo: (String) -> Unit = { message ->
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    if (!isPreview) {
        LaunchedEffect(Unit) {
            viewModel.loadProfile()
            viewModel.loadStudents()
            viewModel.loadBiometricState()
        }
        LaunchedEffect(uiState.biometricMessage) {
            uiState.biometricMessage?.let { message ->
                snackbarHostState.showSnackbar(message)
                viewModel.clearBiometricMessage()
            }
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Konfigurasi Profil",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary,
                        letterSpacing = (-0.5).sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Atmospheric Background Glow
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                        center = Offset(size.width, size.height * 0.5f),
                        radius = size.width
                    )
                )
            }

            when (screenState) {
                is ProfilUiState.Loading -> {
                    ShimmerProfil()
                }
                is ProfilUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Sdm3EmptyState(
                            title = "Data Profil Tidak Tersedia",
                            message = "Data profil Anda belum dapat dimuat saat ini.",
                            style = EmptyStateStyle.Neutral
                        )
                    }
                }
                is ProfilUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Sdm3ErrorState(
                            title = "Gagal Memuat",
                            message = screenState.message,
                            style = ErrorStateStyle.Generic,
                            primaryAction = {
                                Sdm3Button(
                                    text = "Coba Lagi",
                                    onClick = { viewModel.loadProfile() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        )
                    }
                }
                is ProfilUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileHeader(
                            name = uiState.name,
                            phone = uiState.phone,
                            email = uiState.email,
                            avatarUrl = uiState.avatarUrl,
                            isUploadingAvatar = uiState.isUploadingAvatar,
                            onAvatarClick = {
                                if (!isPreview) viewModel.uploadAvatar()
                            },
                            onEditClick = {
                                if (!isPreview) viewModel.startEdit()
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        val activeStudent = uiState.students.firstOrNull { it.id == selectedStudentId }
                            ?: uiState.students.firstOrNull()
                        val canSwitch = uiState.students.size > 1
                        SectionHeader(title = "IDENTITAS AKADEMIK", modifier = Modifier.alpha(0.5f))
                        Spacer(modifier = Modifier.height(12.dp))
                        StudentMiniCard(
                            student = activeStudent,
                            childCount = uiState.students.size,
                            canSwitch = canSwitch,
                            onSwitchClick = { if (canSwitch) showStudentSheet = true }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        SectionHeader(title = "PENGATURAN SISTEM", modifier = Modifier.alpha(0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Sdm3Card(padding = 8.dp) {
                            Column {
                                BiometricSettingsRow(
                                    enabled = uiState.biometricEnabled,
                                    onToggle = { if (!isPreview) viewModel.setBiometricEnabled(it) },
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = colorScheme.primary.copy(alpha = 0.05f)
                                )
                                val settingsItems = buildList {
                                    if (hasTeacherAccess && onOpenTeacherPanel != null) {
                                        add(SettingsItem(stringResource(Res.string.teacher_panel_menu), Icons.Outlined.Groups, colorScheme.primary, onOpenTeacherPanel))
                                    }
                                    if (hasTeacherAccess && onOpenAbsensiSaya != null) {
                                        add(SettingsItem(stringResource(Res.string.teacher_absensi_saya_menu), Icons.Outlined.LocationOn, colorScheme.secondary, onOpenAbsensiSaya))
                                    }
                                    add(SettingsItem("Notifikasi", Icons.Outlined.Notifications, colorScheme.primary, onNotifikasiSetting))
                                    add(SettingsItem("Preferensi Bahasa", Icons.Outlined.Language, colorScheme.primary, { showInfo("Bahasa Indonesia aktif") }))
                                    add(SettingsItem("Tentang Aplikasi", Icons.Outlined.Info, colorScheme.primary, { showInfo("${AppBranding.SCHOOL_NAME} v${com.sdm3.parent.APP_VERSION_NAME}") }))
                                    add(SettingsItem("Pusat Bantuan", Icons.AutoMirrored.Outlined.Chat, colorScheme.primary, { PlatformActions.openUrl(AppBranding.HELP_URL) }))
                                    add(SettingsItem("Kebijakan Privasi", Icons.Outlined.VerifiedUser, colorScheme.primary, { PlatformActions.openUrl(AppBranding.PRIVACY_URL) }))
                                }
                                settingsItems.forEachIndexed { index, item ->
                                    SettingsItemRow(item = item, trailing = if (item.label == "Preferensi Bahasa") "ID" else null)
                                    if (index < settingsItems.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            color = colorScheme.primary.copy(alpha = 0.05f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Spacer(modifier = Modifier.height(16.dp))

                        Sdm3OutlinedButton(
                            text = "Penghapusan Akun",
                            onClick = onAccountDeletion,
                            icon = Icons.Outlined.DeleteForever,
                            contentColor = colorScheme.error,
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Sdm3Button(
                            text = "Keluar Dari Sesi",
                            onClick = { showLogoutDialog = true },
                            containerColor = colorScheme.error,
                            contentColor = colorScheme.onError,
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${AppBranding.SCHOOL_NAME.uppercase()} v${com.sdm3.parent.APP_VERSION_NAME}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = colorScheme.primary.copy(alpha = 0.3f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = AppBranding.SCHOOL_NAME,
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.primary.copy(alpha = 0.2f)
                            )
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }

    if (!isPreview && uiState.isEditing) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelEdit() },
            shape = RoundedCornerShape(24.dp),
            containerColor = colorScheme.surface,
            title = {
                Text(
                    "Edit Profil",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Sdm3TextField(
                        value = uiState.editedName,
                        onValueChange = { viewModel.updateEditedName(it) },
                        label = "NAMA LENGKAP",
                        leadingIcon = Icons.Outlined.Person
                    )
                    Sdm3TextField(
                        value = uiState.editedPhone,
                        onValueChange = { viewModel.updateEditedPhone(it) },
                        label = "NOMOR TELEPON",
                        leadingIcon = Icons.Outlined.Phone
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.updateProfile() },
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelEdit() }) {
                    Text("Batal", fontWeight = FontWeight.Bold, color = colorScheme.primary)
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = colorScheme.surface,
            title = { 
                Text(
                    "Konfirmasi Keluar", 
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                ) 
            },
            text = { 
                Text(
                    "Anda akan mengakhiri sesi aktif pada perangkat ini. Perlu masuk kembali untuk mengakses aplikasi.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        if (!isPreview) {
                            viewModel.logout()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                ) {
                    Text("Keluar", color = colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", fontWeight = FontWeight.Bold, color = colorScheme.primary)
                }
            }
        )
    }

    if (!isPreview && showStudentSheet && uiState.students.size > 1) {
        PilihAnakBottomSheet(
            students = uiState.students,
            selectedStudentId = selectedStudentId,
            onStudentSelected = { newId ->
                showStudentSheet = false
                onSwitchStudent(newId)
            },
            onDismiss = { showStudentSheet = false }
        )
    }
}

@Composable
private fun ShimmerProfil() {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Sdm3Card(padding = 20.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(70.dp).clip(RoundedCornerShape(20.dp)).shimmerEffect()
                )
                Spacer(modifier = Modifier.width(20.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.fillMaxWidth(0.5f).height(18.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(0.4f).height(14.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier.width(80.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(0.4f).height(14.dp).clip(RoundedCornerShape(4.dp)).alpha(0.5f).shimmerEffect()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Sdm3Card(padding = 16.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).shimmerEffect()
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.fillMaxWidth(0.4f).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(0.6f).height(14.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                    )
                }
                Box(
                    modifier = Modifier.width(48.dp).height(22.dp).clip(RoundedCornerShape(6.dp)).shimmerEffect()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxWidth(0.3f).height(14.dp).clip(RoundedCornerShape(4.dp)).alpha(0.5f).shimmerEffect()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Sdm3Card(padding = 8.dp) {
            Column {
                repeat(5) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect()
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier.weight(1f).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
                        )
                        Box(
                            modifier = Modifier.size(18.dp).clip(CircleShape).shimmerEffect()
                        )
                    }
                    if (index < 4) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = colorScheme.primary.copy(alpha = 0.05f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect()
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    phone: String,
    email: String,
    avatarUrl: String?,
    isUploadingAvatar: Boolean,
    onAvatarClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val statusSuccess = statusSuccessColor()

    Sdm3Card(padding = 20.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onAvatarClick),
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(20.dp),
                    color = colorScheme.primary.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (!avatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Foto profil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = colorScheme.primary
                            )
                        }
                    }
                }
                if (isUploadingAvatar) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(overlayScrimColor(alpha = 0.35f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
                if (phone.isNotBlank()) {
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                if (email.isNotBlank()) {
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = statusSuccess.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = " TERVERIFIKASI ",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = statusSuccess,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = colorScheme.primary.copy(alpha = 0.05f)
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentMiniCard(
    student: StudentDto?,
    childCount: Int,
    canSwitch: Boolean,
    onSwitchClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Sdm3Card(
        modifier = if (canSwitch) Modifier.clickable(onClick = onSwitchClick) else Modifier,
        padding = 16.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = colorScheme.primaryContainer,
                    border = BorderStroke(2.dp, colorScheme.surface)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = com.sdm3.parent.core.util.nameInitials(student?.name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student?.name ?: "Belum ada siswa",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                    Text(
                        text = student?.let { com.sdm3.parent.core.util.formatClassName(it.className) } ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    if (student?.nisn != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "NISN: ${student.nisn}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.primary.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (student?.portalId != null) {
                        Text(
                            text = "ID Akun: ${student.portalId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.primary.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (student?.waliKelas != null) {
                        Text(
                            text = "Wali Kelas: ${student.waliKelas}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.primary.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Surface(
                    color = colorScheme.secondary,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "AKTIF",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (canSwitch) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colorScheme.primary.copy(alpha = 0.06f))
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$childCount anak terdaftar",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.SwapHoriz,
                            contentDescription = "Ganti anak",
                            tint = colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ganti Siswa",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BiometricSettingsRow(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = colorScheme.primary.copy(alpha = 0.05f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Outlined.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colorScheme.primary,
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Login Biometrik",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
            )
            Text(
                text = "Masuk cepat dengan sidik jari atau Face ID",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.primary.copy(alpha = 0.5f),
            )
        }
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
        )
    }
}

@Composable
private fun SettingsItemRow(
    item: SettingsItem,
    trailing: String? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = item.color.copy(alpha = 0.05f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = item.color
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            color = colorScheme.primary
        )
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = colorScheme.primary.copy(alpha = 0.2f)
        )
    }
}

data class SettingsItem(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Preview
@Composable
private fun ProfilAkunScreenPreview() {
    SDM3Theme {
        ProfilAkunScreen(onNotifikasiSetting = {}, onAccountDeletion = {}, onLogout = {})
    }
}
