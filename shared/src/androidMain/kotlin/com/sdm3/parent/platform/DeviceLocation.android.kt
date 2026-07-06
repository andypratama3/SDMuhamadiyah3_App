package com.sdm3.parent.platform

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

private const val LOCATION_TIMEOUT_MS = 20_000L

actual object DeviceLocationService {

    actual suspend fun permissionState(): LocationPermissionState {
        val context = AndroidPlatformProvider.activity ?: return LocationPermissionState.NEED_PERMISSION
        if (!hasLocationPermission(context)) {
            val shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
            if (AndroidPlatformProvider.locationPermissionRequested && !shouldExplain) {
                return LocationPermissionState.DENIED_PERMANENTLY
            }
            return LocationPermissionState.NEED_PERMISSION
        }
        if (!isLocationProviderEnabled(context)) {
            return LocationPermissionState.LOCATION_SERVICES_OFF
        }
        return LocationPermissionState.GRANTED
    }

    actual suspend fun requestPermission(): Boolean {
        val requester = AndroidPlatformProvider.requestLocationPermission
            ?: return hasLocationPermission(AndroidPlatformProvider.activity)
        AndroidPlatformProvider.locationPermissionRequested = true
        return requester()
    }

    actual fun openAppSettings() {
        val activity = AndroidPlatformProvider.activity ?: return
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null),
        )
        activity.startActivity(intent)
    }

    actual fun openLocationSettings() {
        val activity = AndroidPlatformProvider.activity ?: return
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    actual suspend fun getCurrentLocation(): DeviceLocation {
        val context = AndroidPlatformProvider.activity
            ?: throw IllegalStateException("Aktivitas aplikasi belum siap")
        if (!hasLocationPermission(context)) {
            throw IllegalStateException("Izin lokasi belum diberikan")
        }
        if (!isLocationProviderEnabled(context)) {
            throw IllegalStateException("GPS belum aktif")
        }

        val client = LocationServices.getFusedLocationProviderClient(context)
        val cancellation = CancellationTokenSource()

        val fusedLocation = withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
            runCatching {
                client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellation.token).await()
            }.getOrNull()
        }
        if (fusedLocation != null) {
            return fusedLocation.toDeviceLocation()
        }

        return awaitSingleFusedUpdate(client)
    }

    @SuppressLint("MissingPermission")
    actual fun watchLocation(onUpdate: (DeviceLocation) -> Unit): () -> Unit {
        val context = AndroidPlatformProvider.activity ?: return {}
        if (!hasLocationPermission(context) || !isLocationProviderEnabled(context)) return {}

        val client = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3_000L)
            .setMinUpdateIntervalMillis(2_000L)
            .setMinUpdateDistanceMeters(5f)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { onUpdate(it.toDeviceLocation()) }
            }
        }

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        return { client.removeLocationUpdates(callback) }
    }

    @SuppressLint("MissingPermission")
    private suspend fun awaitSingleFusedUpdate(
        client: com.google.android.gms.location.FusedLocationProviderClient,
    ): DeviceLocation = suspendCancellableCoroutine { continuation ->
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1_000L)
            .setMaxUpdates(1)
            .build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                client.removeLocationUpdates(this)
                val location = result.lastLocation
                if (location != null && continuation.isActive) {
                    continuation.resume(location.toDeviceLocation())
                } else if (continuation.isActive) {
                    continuation.resumeWithException(IllegalStateException("Lokasi tidak tersedia"))
                }
            }
        }
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        continuation.invokeOnCancellation { client.removeLocationUpdates(callback) }
    }

    private fun isLocationProviderEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }

    private fun hasLocationPermission(context: Context?): Boolean {
        context ?: return false
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    private fun Location.toDeviceLocation(): DeviceLocation =
        DeviceLocation(
            latitude = latitude,
            longitude = longitude,
            accuracyMeters = accuracy.toDouble().coerceAtLeast(0.0),
        )
}

actual object DeviceIdentity {
    actual fun deviceId(): String {
        val context = AndroidPlatformProvider.activity ?: return "android-unknown"
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val seed = listOf(androidId, Build.MANUFACTURER, Build.MODEL).joinToString("|")
        return seed.take(120)
    }

    actual fun deviceFingerprint(): String {
        val seed = listOf(
            Build.BOARD,
            Build.HARDWARE,
            Build.DEVICE,
            Runtime.getRuntime().availableProcessors().toString(),
        ).joinToString("|")
        return seed.take(120)
    }
}
