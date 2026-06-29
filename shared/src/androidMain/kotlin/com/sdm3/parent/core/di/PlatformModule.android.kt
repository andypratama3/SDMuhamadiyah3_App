package com.sdm3.parent.core.di

import android.content.Context
import com.liftric.kvault.KVault
import com.sdm3.parent.cache.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { KVault(get()) }
    single { DatabaseDriverFactory(context = androidContext()) }
}
