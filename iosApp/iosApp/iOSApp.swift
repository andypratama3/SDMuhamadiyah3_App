import FirebaseCore
import FirebaseMessaging
import Shared
import UIKit
import UserNotifications

@main
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    private var lastBridgedFcmToken: String?
    private var hasApnsToken = false
    private var pendingFcmToken: String?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self
        requestPushAuthorization(application)
        return true
    }

    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        let configuration = UISceneConfiguration(
            name: "Default Configuration",
            sessionRole: connectingSceneSession.role
        )
        configuration.delegateClass = SceneDelegate.self
        return configuration
    }

    private func requestPushAuthorization(_ application: UIApplication) {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            if let error = error {
                debugLog("Push authorization error: \(error.localizedDescription)")
                return
            }

            if !granted {
                debugLog("Push authorization denied by user")
                return
            }

            DispatchQueue.main.async {
                application.registerForRemoteNotifications()
            }
        }
    }

    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        Messaging.messaging().apnsToken = deviceToken
        hasApnsToken = true

        #if DEBUG
        let hexToken = deviceToken.map { String(format: "%02.2hhx", $0) }.joined()
        debugLog("APNS_DEVICE_TOKEN => \(hexToken)")
        #endif

        if let pendingFcmToken {
            self.pendingFcmToken = nil
            bridgeFcmToken(pendingFcmToken)
        } else {
            fetchAndBridgeFcmToken()
        }
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        debugLog("APNS_REGISTER_FAILED => \(error.localizedDescription)")
        debugLog("Hint: aktifkan Push Notifications di Xcode (Signing & Capabilities) dan refresh provisioning profile.")
    }

    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable: Any],
        fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
        Messaging.messaging().appDidReceiveMessage(userInfo)
        completionHandler(.newData)
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let token = fcmToken, !token.isEmpty else { return }
        if hasApnsToken {
            bridgeFcmToken(token)
        } else {
            pendingFcmToken = token
        }
    }

    private func fetchAndBridgeFcmToken() {
        Messaging.messaging().token { [weak self] token, error in
            if let error = error {
                debugLog("SDM3_FCM_TOKEN_FETCH_FAILED => \(error.localizedDescription)")
                return
            }
            guard let token = token, !token.isEmpty else { return }
            guard let self else { return }
            if self.hasApnsToken {
                self.bridgeFcmToken(token)
            } else {
                self.pendingFcmToken = token
            }
        }
    }

    private func bridgeFcmToken(_ token: String?) {
        guard let token = token, !token.isEmpty else { return }
        guard token != lastBridgedFcmToken else { return }
        lastBridgedFcmToken = token
        debugLog("SDM3_FCM_TOKEN => \(token)")
        FcmBridge.shared.onTokenReceived(token: token)
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound, .badge])
    }

    private func debugLog(_ message: String) {
        #if DEBUG
        print(message)
        #endif
    }
}
