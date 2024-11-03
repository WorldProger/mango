package com.worldproger.mango.data.storage

import org.koin.core.qualifier.named
import org.koin.dsl.module

fun storageModule() = module {
    single { TokenStorage(get(), get(qualifier = named("bearer"))) }
}