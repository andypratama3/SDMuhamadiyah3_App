@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.sdm3.parent.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLAuthorizationStatus
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
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

private const val LOCATION_TIMEOUT_MS = 20_000L

private fun CLLocation.toDeviceLocation(): DeviceLocation = coordinate.useContents {
    DeviceLocation(
        latitude = latitude,
        longitude = longitude,
        accuracyMeters = this@toDeviceLocation.horizontalAccuracy.coerceAtLeast(0.0),
    )
}

private class IosLocationCoordinator : NSObject(), CLLocationManagerDelegateProtocol {

    private val manager = CLLocationManager()

    private var currentCallback: ((Result<DeviceLocation>) -> Unit)? = null
    private var currentPermissionCallback: ((Boolean) -> Unit)? = null
    private var watchCallback: ((DeviceLocation) -> Unit)? = null

    init {
        manager.delegate = this
        manager.desiredAccuracy = kCLLocationAccuracyBest
    }

    fun permissionState(): LocationPermissionState {
        if (!CLLocationManager.locationServicesEnabled()) return LocationPermissionState.LOCATION_SERVICES_OFF
        return when (CLLocationManager.authorizationStatus()) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> LocationPermissionState.GRANTED
            kCLAuthorizationStatusNotDetermined -> LocationPermissionState.NEED_PERMISSION
            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted -> LocationPermissionState.DENIED_PERMANENTLY
            else -> LocationPermissionState.NEED_PERMISSION
        }
    }

    fun requestPermission(onResult: (Boolean) -> Unit) {
        val status = CLLocationManager.authorizationStatus()
        if (status == kCLAuthorizationStatusNotDetermined) {
            currentPermissionCallback = onResult
            manager.requestWhenInUseAuthorization()
            return
        }
        onResult(isGranted(status))
    }

    fun getCurrentLocation(onResult: (Result<DeviceLocation>) -> Unit) {
        if (currentCallback != null) {
            onResult(Result.failure(IllegalStateException("Pencarian lokasi sedang berjalan")))
            return
        }
        if (permissionState() != LocationPermissionState.GRANTED) {
            onResult(Result.failure(IllegalStateException("Izin lokasi belum diberikan")))
            return
        }
        currentCallback = onResult
        manager.startUpdatingLocation()
    }

    fun watch(onUpdate: (DeviceLocation) -> Unit): () -> Unit {
        watchCallback = onUpdate
        if (permissionState() == LocationPermissionState.GRANTED) {
            manager.startUpdatingLocation()
        }
        return {
            watchCallback = null
            manager.stopUpdatingLocation()
        }
    }

    fun cancelPendingLocation() {
        currentCallback = null
        manager.stopUpdatingLocation()
    }

    private fun isGranted(status: CLAuthorizationStatus): Boolean =
        status == kCLAuthorizationStatusAuthorizedWhenInUse || status == kCLAuthorizationStatusAuthorizedAlways

    private fun onAuthorizationChanged(status: CLAuthorizationStatus) {
        val callback = currentPermissionCallback ?: return
        currentPermissionCallback = null
        val granted = CLLocationManager.locationServicesEnabled() && isGranted(status)
        callback(granted)
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        onAuthorizationChanged(CLLocationManager.authorizationStatus())
    }

    @Suppress("DEPRECATION")
    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) {
        onAuthorizationChanged(didChangeAuthorizationStatus)
    }

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
        val deviceLocation = location.toDeviceLocation()
        val pending = currentCallback
        if (pending != null) {
            currentCallback = null
            manager.stopUpdatingLocation()
            pending(Result.success(deviceLocation))
        }
        watchCallback?.invoke(deviceLocation)
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        val failure = Result.failure<DeviceLocation>(
            IllegalStateException(didFailWithError.localizedDescription),
        )
        val pending = currentCallback
        if (pending != null) {
            currentCallback = null
            manager.stopUpdatingLocation()
            pending(failure)
        }
    }
}

private val iosLocationCoordinator = IosLocationCoordinator()

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
                continuation.invokeOnCancellation {
                    iosLocationCoordinator.cancelPendingLocation()
                }
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