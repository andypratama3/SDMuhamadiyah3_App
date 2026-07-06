package com.sdm3.parent.core.designsystem

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyle
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowScene
import platform.UIKit.addChildViewController
import platform.UIKit.didMoveToParentViewController
import platform.UIKit.willMoveToParentViewController

@OptIn(ExperimentalForeignApi::class)
internal object StatusBarAppearanceController {
    var prefersDarkStatusBarIcons: Boolean = true
        private set

    val preferredStatusBarStyle: UIStatusBarStyle
        get() = if (prefersDarkStatusBarIcons) {
            UIStatusBarStyleDarkContent
        } else {
            UIStatusBarStyleLightContent
        }

    fun apply(prefersDarkStatusBarIcons: Boolean) {
        if (this.prefersDarkStatusBarIcons == prefersDarkStatusBarIcons) return
        this.prefersDarkStatusBarIcons = prefersDarkStatusBarIcons
        refresh()
    }

    private fun refresh() {
        keyRootViewController()?.setNeedsStatusBarAppearanceUpdate()
    }

    private fun keyRootViewController(): UIViewController? {
        val application = UIApplication.sharedApplication
        val sceneWindow = application.connectedScenes
            .mapNotNull { it as? UIWindowScene }
            .firstNotNullOfOrNull { it.keyWindow }
        return sceneWindow?.rootViewController ?: application.keyWindow?.rootViewController
    }
}

@OptIn(ExperimentalForeignApi::class)
internal class StatusBarHostViewController(
    private val content: UIViewController,
) : UIViewController(nibName = null, bundle = null) {

    override fun loadView() {
        super.loadView()
        content.willMoveToParentViewController(this)
        content.view.setAutoresizingMask(
            UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
        )
        content.view.setFrame(view.bounds)
        view.addSubview(content.view)
        addChildViewController(content)
        content.didMoveToParentViewController(this)
    }

    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        content.view.setFrame(view.bounds)
    }

    override fun preferredStatusBarStyle(): UIStatusBarStyle =
        StatusBarAppearanceController.preferredStatusBarStyle

    override fun prefersStatusBarHidden(): Boolean = false
}
