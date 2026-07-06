package com.sdm3.parent.core.notification

/**
 * Runtime gate for parent-only push delivery (bound from [App] at startup).
 */
object PushAccessGate {
    var hasParentAccess: () -> Boolean = { false }

    /** Callable from iOS Swift via Shared framework. */
    fun canReceivePush(): Boolean = hasParentAccess()
}
