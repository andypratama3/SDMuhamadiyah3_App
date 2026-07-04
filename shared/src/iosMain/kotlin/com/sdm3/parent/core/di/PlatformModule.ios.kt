package com.sdm3.parent.core.di

import com.liftric.kvault.KVault
import com.sdm3.parent.cache.DatabaseDriverFactory
import com.sdm3.parent.core.security.InstallState
import com.sdm3.parent.core.security.IosInstallState
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { KVault() }
    single { DatabaseDriverFactory() }
    single<InstallState> { IosInstallState() }
}
