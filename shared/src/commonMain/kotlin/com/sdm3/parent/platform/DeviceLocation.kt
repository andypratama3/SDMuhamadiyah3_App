package com.sdm3.parent.platform

data class DeviceLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Double,
)

enum class LocationPermissionState {
    GRANTED,
    NEED_PERMISSION,
    DENIED_PERMANENTLY,
    LOCATION_SERVICES_OFF,
}

expect object DeviceLocationService {
    suspend fun permissionState(): LocationPermissionState
    suspend fun requestPermission(): Boolean
    fun openAppSettings()
    fun openLocationSettings()
    suspend fun getCurrentLocation(): DeviceLocation
    fun watchLocation(onUpdate: (DeviceLocation) -> Unit): () -> Unit
}

expect object DeviceIdentity {
    fun deviceId(): String
    fun deviceFingerprint(): String
}
