package com.sdm3.parent.feature.guru

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.EmployeeSelfAttendanceRecordDto
import com.sdm3.parent.domain.repository.EmployeeSelfAttendanceRepositoryContract
import com.sdm3.parent.platform.DeviceLocation
import com.sdm3.parent.platform.DeviceLocationService
import com.sdm3.parent.platform.LocationPermissionState

data class AbsensiSayaUiState(
    val employeeName: String = "",
    val employeeNip: String? = null,
    val date: String = "",
    val attendance: EmployeeSelfAttendanceRecordDto? = null,
    val canCheckIn: Boolean = false,
    val canCheckOut: Boolean = false,
    val liveLocation: DeviceLocation? = null,
    val locationPermission: LocationPermissionState = LocationPermissionState.NEED_PERMISSION,
    val locationStatus: String = "Menunggu izin lokasi...",
    val isSubmitting: Boolean = false,
    val isRequestingPermission: Boolean = false,
    val permissionRequestAttempted: Boolean = false,
    val isEmployeeProfileMissing: Boolean = false,
    val successMessage: String? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
) : ScreenState {
    val isLocationReady: Boolean
        get() {
            val loc = liveLocation ?: return false
            return locationPermission == LocationPermissionState.GRANTED &&
                loc.accuracyMeters <= 100.0
        }

    val showLocationSettingsShortcut: Boolean
        get() = locationPermission == LocationPermissionState.DENIED_PERMANENTLY ||
            locationPermission == LocationPermissionState.LOCATION_SERVICES_OFF ||
            (locationPermission == LocationPermissionState.NEED_PERMISSION && permissionRequestAttempted)
}

class AbsensiSayaViewModel(
    private val repository: EmployeeSelfAttendanceRepositoryContract,
) : BaseViewModel<AbsensiSayaUiState>(AbsensiSayaUiState()) {

    private var stopWatching: (() -> Unit)? = null

    fun load(showFullLoading: Boolean = true) {
        launchSafely {
            if (showFullLoading) {
                updateState { it.copy(isLoading = true, errorMessage = null, isEmployeeProfileMissing = false) }
            }
            when (val result = repository.getToday()) {
                is ApiResult.Success -> {
                    val payload = result.data
                    updateState {
                        it.copy(
                            employeeName = payload.employee.name,
                            employeeNip = payload.employee.nip,
                            date = payload.date,
                            attendance = payload.attendance,
                            canCheckIn = payload.actions.canCheckIn,
                            canCheckOut = payload.actions.canCheckOut,
                            isLoading = false,
                            isEmployeeProfileMissing = false,
                            errorMessage = null,
                        )
                    }
                }
                is ApiResult.Error -> {
                    val message = result.error.toUserMessage()
                    val missingProfile = message.contains("pegawai", ignoreCase = true) ||
                        message.contains("Profil pegawai", ignoreCase = true)
                    updateState {
                        it.copy(
                            isLoading = false,
                            isEmployeeProfileMissing = missingProfile,
                            errorMessage = message,
                        )
                    }
                }
            }
        }
    }

    fun onScreenResume() {
        refreshLocationAccess()
        if (uiState.value.employeeName.isNotBlank()) {
            load(showFullLoading = false)
        }
    }

    fun refreshLocationAccess() {
        launchSafely {
            val state = DeviceLocationService.permissionState()
            updateState {
                it.copy(
                    locationPermission = state,
                    locationStatus = statusMessageFor(state),
                )
            }
            if (state == LocationPermissionState.GRANTED) {
                startLocationTrackingInternal()
            } else {
                stopWatching?.invoke()
                stopWatching = null
                updateState { it.copy(liveLocation = null) }
            }
        }
    }

    fun requestLocationAccess() {
        launchSafely {
            updateState { it.copy(isRequestingPermission = true, errorMessage = null) }
            DeviceLocationService.requestPermission()
            val state = DeviceLocationService.permissionState()
            updateState {
                it.copy(
                    isRequestingPermission = false,
                    permissionRequestAttempted = true,
                    locationPermission = state,
                    locationStatus = statusMessageFor(state),
                )
            }
            if (state == LocationPermissionState.GRANTED) {
                startLocationTrackingInternal()
            }
        }
    }

    fun openAppSettings() {
        DeviceLocationService.openAppSettings()
    }

    fun openLocationSettings() {
        DeviceLocationService.openLocationSettings()
    }

    fun consumeSuccessMessage() {
        updateState { it.copy(successMessage = null) }
    }

    private fun startLocationTrackingInternal() {
        stopWatching?.invoke()
        stopWatching = DeviceLocationService.watchLocation { location ->
            updateState {
                it.copy(
                    liveLocation = location,
                    locationPermission = LocationPermissionState.GRANTED,
                    locationStatus = "GPS aktif • akurasi ${location.accuracyMeters.toInt()} m",
                )
            }
        }
        if (uiState.value.liveLocation == null) {
            updateState { it.copy(locationStatus = "Mencari sinyal GPS...") }
        }
    }

    fun checkIn() {
        submitAttendance(isCheckIn = true)
    }

    fun checkOut() {
        submitAttendance(isCheckIn = false)
    }

    private fun submitAttendance(isCheckIn: Boolean) {
        launchSafely {
            val permission = DeviceLocationService.permissionState()
            if (permission != LocationPermissionState.GRANTED) {
                updateState {
                    it.copy(
                        locationPermission = permission,
                        locationStatus = statusMessageFor(permission),
                        errorMessage = "Izin lokasi diperlukan untuk absensi.",
                    )
                }
                return@launchSafely
            }

            updateState { it.copy(isSubmitting = true, errorMessage = null) }
            val location = uiState.value.liveLocation ?: run {
                when (val current = runCatching { DeviceLocationService.getCurrentLocation() }.getOrNull()) {
                    null -> {
                        updateState {
                            it.copy(
                                isSubmitting = false,
                                errorMessage = "Gagal mendapatkan lokasi. Pastikan GPS aktif dan coba lagi.",
                            )
                        }
                        return@launchSafely
                    }
                    else -> current
                }
            }

            if (location.accuracyMeters > 100.0) {
                updateState {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = "Akurasi GPS rendah (${location.accuracyMeters.toInt()} m). Tunggu sinyal lebih baik.",
                    )
                }
                return@launchSafely
            }

            val result = if (isCheckIn) {
                repository.checkIn(location.latitude, location.longitude)
            } else {
                repository.checkOut(location.latitude, location.longitude)
            }

            when (result) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            attendance = result.data,
                            isSubmitting = false,
                            errorMessage = null,
                            successMessage = if (isCheckIn) {
                                "Check-in berhasil. Selamat bekerja!"
                            } else {
                                "Check-out berhasil. Terima kasih!"
                            },
                        )
                    }
                    load(showFullLoading = false)
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(isSubmitting = false, errorMessage = result.error.toUserMessage())
                    }
                }
            }
        }
    }

    fun onScreenDispose() {
        stopWatching?.invoke()
        stopWatching = null
    }

    private fun statusMessageFor(state: LocationPermissionState): String = when (state) {
        LocationPermissionState.GRANTED -> uiState.value.locationStatus.takeIf {
            it.isNotBlank() && !it.startsWith("Menunggu") && !it.startsWith("Aplikasi")
        } ?: "Mencari sinyal GPS..."
        LocationPermissionState.NEED_PERMISSION ->
            "Aplikasi membutuhkan izin lokasi untuk absensi check-in/out di area sekolah."
        LocationPermissionState.DENIED_PERMANENTLY ->
            "Izin lokasi ditolak. Izinkan akses lokasi di pengaturan aplikasi."
        LocationPermissionState.LOCATION_SERVICES_OFF ->
            "GPS perangkat belum aktif. Aktifkan layanan lokasi di pengaturan."
    }
}
