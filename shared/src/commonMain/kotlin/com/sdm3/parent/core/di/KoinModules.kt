package com.sdm3.parent.core.di

import org.koin.core.module.Module

val allAppModules: List<Module> = listOf(
    configModule,
    platformModule(),
    securityModule,
    notificationModule,
    networkModule,
    apiModule,
    repositoryModule,
    viewModelModule
)
