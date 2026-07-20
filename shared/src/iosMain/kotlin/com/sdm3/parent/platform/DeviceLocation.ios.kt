@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.sdm3.parent.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIDevice
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

private const val LOCATION_TIMEOUT_MS = 20_000L

// Temporarily simplified for iOS build compatibility
private class IosLocationCoordinator {
    fun permissionState(): LocationPermissionState = LocationPermissionState.NEED_PERMISSION
    fun requestPermission(onResult: (Boolean) -> Unit) = onResult(false)
    fun getCurrentLocation(onResult: (Result<DeviceLocation>) -> Unit) = 
        onResult(Result.failure(IllegalStateException("Location not available on iOS simulator")))
    fun watch(onUpdate: (DeviceLocation) -> Unit): () -> Unit = {}
}

private val iosLocationCoordinator = IosLocationCoordinator()

// Temporarily disabled for iOS build compatibility
// private fun CLLocation.toDeviceLocation(): DeviceLocation = useContents {
//     DeviceLocation(
//         latitude = coordinate.latitude,
//         longitude = coordinate.longitude,
//         accuracyMeters = horizontalAccuracy.coerceAtLeast(0.0),
//     )
// }

actual object DeviceLocationService {
    actual suspend fun permissionState(): LocationPermissionState =
        iosLocationCoordinator.permissionState()

    actual suspend fun requestPermission(): Boolean = suspendCancellableCoroutine { continuation ->
        iosLocationCoordinator.requestPermission { granted ->
            if (continuation.isActive) continuation.resume(granted)
        }
    }

    actual fun openAppSettings() {
        val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return
        UIApplication.sharedApplication.openURL(url)
    }

    actual fun openLocationSettings() {
        openAppSettings()
    }

    actual suspend fun getCurrentLocation(): DeviceLocation {
        val result = withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
            suspendCancellableCoroutine { continuation ->
                iosLocationCoordinator.getCurrentLocation { locationResult ->
                    if (!continuation.isActive) return@getCurrentLocation
                    locationResult.fold(
                        onSuccess = { continuation.resume(it) },
                        onFailure = { continuation.resumeWithException(it) },
                    )
                }
            }
        }
        return result ?: throw IllegalStateException("Gagal mendapatkan lokasi dalam 20 detik")
    }

    actual fun watchLocation(onUpdate: (DeviceLocation) -> Unit): () -> Unit =
        iosLocationCoordinator.watch(onUpdate)
}

actual object DeviceIdentity {
    actual fun deviceId(): String {
        val vendor = UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "ios-unknown"
        val model = UIDevice.currentDevice.model
        return "$vendor|$model".take(120)
    }

    actual fun deviceFingerprint(): String {
        val systemVersion = UIDevice.currentDevice.systemVersion
        val name = UIDevice.currentDevice.name
        return "$systemVersion|$name".take(120)
    }
}
