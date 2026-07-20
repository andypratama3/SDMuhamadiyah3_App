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

#Preview("SDM3 Parent - iPhone") {
    ContentView()
}

#Preview("SDM3 Parent - Light") {
    ContentView()
        .preferredColorScheme(.light)
}

#Preview("SDM3 Parent - Dark") {
    ContentView()
        .preferredColorScheme(.dark)
}

#Preview("SDM3 Parent - iPad") {
    ContentView()
        .previewDevice("iPad Pro (11-inch) (4th generation)")
}
