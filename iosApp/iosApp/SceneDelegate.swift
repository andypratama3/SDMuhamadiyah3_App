import Shared
import UIKit

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?
    private var qrScanObserver: NSObjectProtocol?
    private var avatarPickObserver: NSObjectProtocol?

    func scene(
        _ scene: UIScene,
        willConnectTo session: UISceneSession,
        options connectionOptions: UIScene.ConnectionOptions
    ) {
        guard let windowScene = scene as? UIWindowScene else { return }

        let composeController = MainViewControllerKt.MainViewController()
        composeController.view.backgroundColor = UIColor { traitCollection in
            traitCollection.userInterfaceStyle == .dark
                ? UIColor.black
                : UIColor(red: 0.969, green: 0.973, blue: 0.980, alpha: 1.0)
        }
        let window = UIWindow(windowScene: windowScene)
        window.rootViewController = composeController
        window.makeKeyAndVisible()
        self.window = window

        qrScanObserver = NotificationCenter.default.addObserver(
            forName: NSNotification.Name("SDM3QrScanRequest"),
            object: nil,
            queue: .main
        ) { [weak self] _ in
            guard let root = self?.window?.rootViewController else {
                IosQrScanCallbackDispatcher.shared.deliverResult(value: nil)
                return
            }
            QrCodeScannerPresenter.present(from: root) { result in
                IosQrScanCallbackDispatcher.shared.deliverResult(value: result)
            }
        }

        avatarPickObserver = NotificationCenter.default.addObserver(
            forName: NSNotification.Name("SDM3AvatarPickRequest"),
            object: nil,
            queue: .main
        ) { [weak self] _ in
            guard let root = self?.window?.rootViewController else {
                IosAvatarPickCallbackDispatcher.shared.deliverResult(
                    base64Data: nil,
                    fileName: nil,
                    mimeType: nil
                )
                return
            }
            AvatarImagePickerPresenter.present(from: root) { base64Data, fileName, mimeType in
                IosAvatarPickCallbackDispatcher.shared.deliverResult(
                    base64Data: base64Data,
                    fileName: fileName,
                    mimeType: mimeType
                )
            }
        }
    }

    deinit {
        if let qrScanObserver {
            NotificationCenter.default.removeObserver(qrScanObserver)
        }
        if let avatarPickObserver {
            NotificationCenter.default.removeObserver(avatarPickObserver)
        }
    }
}
