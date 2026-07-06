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
import platform.Foundation.NSObject
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIDevice
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

private const val LOCATION_TIMEOUT_MS = 20_000L

private class IosLocationCoordinator : NSObject(), CLLocationManagerDelegateProtocol {
    private val manager = CLLocationManager().apply {
        desiredAccuracy = kCLLocationAccuracyBest
        delegate = this@IosLocationCoordinator
    }

    private var permissionContinuation: ((Boolean) -> Unit)? = null
    private var singleShotContinuation: ((Result<DeviceLocation>) -> Unit)? = null
    private var watchCallback: ((DeviceLocation) -> Unit)? = null

    fun permissionState(): LocationPermissionState {
        if (!CLLocationManager.locationServicesEnabled()) {
            return LocationPermissionState.LOCATION_SERVICES_OFF
        }
        return when (manager.authorizationStatus) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways,
            -> LocationPermissionState.GRANTED
            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted,
            -> LocationPermissionState.DENIED_PERMANENTLY
            kCLAuthorizationStatusNotDetermined -> LocationPermissionState.NEED_PERMISSION
            else -> LocationPermissionState.NEED_PERMISSION
        }
    }

    fun requestPermission(onResult: (Boolean) -> Unit) {
        when (manager.authorizationStatus) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways,
            -> onResult(true)
            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted,
            -> onResult(false)
            kCLAuthorizationStatusNotDetermined -> {
                permissionContinuation = onResult
                manager.requestWhenInUseAuthorization()
            }
            else -> onResult(false)
        }
    }

    fun getCurrentLocation(onResult: (Result<DeviceLocation>) -> Unit) {
        if (permissionState() != LocationPermissionState.GRANTED) {
            onResult(Result.failure(IllegalStateException("Izin lokasi belum diberikan")))
            return
        }
        singleShotContinuation = onResult
        manager.requestLocation()
    }

    fun watch(onUpdate: (DeviceLocation) -> Unit): () -> Unit {
        if (permissionState() != LocationPermissionState.GRANTED) {
            return {}
        }
        watchCallback = onUpdate
        manager.startUpdatingLocation()
        return {
            watchCallback = null
            manager.stopUpdatingLocation()
        }
    }

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
        val deviceLocation = location.toDeviceLocation()
        watchCallback?.invoke(deviceLocation)
        singleShotContinuation?.let { continuation ->
            singleShotContinuation = null
            continuation(Result.success(deviceLocation))
        }
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        singleShotContinuation?.let { continuation ->
            singleShotContinuation = null
            continuation(Result.failure(Exception(didFailWithError.localizedDescription)))
        }
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        resolvePermissionContinuation(manager)
    }

    @Suppress("DEPRECATION")
    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: Int) {
        resolvePermissionContinuation(manager)
    }

    private fun resolvePermissionContinuation(manager: CLLocationManager) {
        permissionContinuation?.let { continuation ->
            permissionContinuation = null
            val granted = manager.authorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
                manager.authorizationStatus == kCLAuthorizationStatusAuthorizedAlways
            continuation(granted)
        }
    }
}

private val iosLocationCoordinator = IosLocationCoordinator()

private fun CLLocation.toDeviceLocation(): DeviceLocation = useContents {
    DeviceLocation(
        latitude = coordinate.latitude,
        longitude = coordinate.longitude,
        accuracyMeters = horizontalAccuracy.coerceAtLeast(0.0),
    )
}

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
