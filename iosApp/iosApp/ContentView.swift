import SwiftUI
import Shared

/// Dipakai hanya untuk SwiftUI Preview. Runtime app memakai `AppDelegate` + `MainViewController`.
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.container)
    }
}


