package com.sdm3.parent.core.di

import com.liftric.kvault.KVault
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { KVault() }
}
