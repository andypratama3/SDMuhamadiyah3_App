import PhotosUI
import Shared
import UIKit

enum AvatarImagePickerPresenter {
    private static var activeDelegate: AvatarImagePickerDelegate?

    static func present(from presenter: UIViewController, completion: @escaping (String?, String?, String?) -> Void) {
        let topPresenter = topViewController(from: presenter) ?? presenter
        let delegate = AvatarImagePickerDelegate { base64Data, fileName, mimeType in
            activeDelegate = nil
            completion(base64Data, fileName, mimeType)
        }
        activeDelegate = delegate

        var configuration = PHPickerConfiguration(photoLibrary: .shared())
        configuration.filter = .images
        configuration.selectionLimit = 1
        let picker = PHPickerViewController(configuration: configuration)
        picker.delegate = delegate
        topPresenter.present(picker, animated: true)
    }

    private static func topViewController(from root: UIViewController) -> UIViewController? {
        var current = root
        while let presented = current.presentedViewController {
            current = presented
        }
        if let navigation = current as? UINavigationController {
            return navigation.visibleViewController ?? navigation
        }
        return current
    }
}

private final class AvatarImagePickerDelegate: NSObject, PHPickerViewControllerDelegate {
    private let onResult: (String?, String?, String?) -> Void

    init(onResult: @escaping (String?, String?, String?) -> Void) {
        self.onResult = onResult
    }

    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        picker.dismiss(animated: true)

        guard let itemProvider = results.first?.itemProvider, itemProvider.canLoadObject(ofClass: UIImage.self) else {
            onResult(nil, nil, nil)
            return
        }

        itemProvider.loadObject(ofClass: UIImage.self) { [weak self] object, _ in
            guard let self else { return }
            guard let image = object as? UIImage, let jpegData = image.jpegData(compressionQuality: 0.85) else {
                DispatchQueue.main.async {
                    self.onResult(nil, nil, nil)
                }
                return
            }

            DispatchQueue.main.async {
                self.onResult(
                    jpegData.base64EncodedString(),
                    "avatar.jpg",
                    "image/jpeg"
                )
            }
        }
    }
}
