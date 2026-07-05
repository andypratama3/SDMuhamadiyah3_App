import Shared
import UIKit

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?

    func scene(
        _ scene: UIScene,
        willConnectTo session: UISceneSession,
        options connectionOptions: UIScene.ConnectionOptions
    ) {
        guard let windowScene = scene as? UIWindowScene else { return }

        let composeController = MainViewControllerKt.MainViewController()
        let window = UIWindow(windowScene: windowScene)
        window.rootViewController = composeController
        window.makeKeyAndVisible()
        self.window = window
    }
}
