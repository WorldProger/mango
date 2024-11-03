package com.worldproger.mango.data.data

import com.worldproger.mango.domain.repository.AuthRepository
import com.worldproger.mango.domain.repository.UserRepository
import org.koin.dsl.module

fun dataModule() = module {
    single { AuthRepository(get(), get(), get()) }
    single { UserRepository(get(), get()) }
}