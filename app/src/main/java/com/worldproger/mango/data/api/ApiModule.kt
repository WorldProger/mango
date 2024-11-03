package com.worldproger.mango.data.api

import android.util.Log
import com.worldproger.mango.data.storage.TokenStorage
import com.worldproger.mango.domain.core.DataError
import com.worldproger.mango.domain.core.Result.Companion.fold
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(ExperimentalSerializationApi::class)
fun apiModule(
    apiUrl: String
): Module = module {

    single<HttpClient> {
        HttpClient().config {
            expectSuccess = true

            install(Logging) {
                level = LogLevel.ALL
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Log.d("HttpClient", message)
                    }
                }
            }

            install(HttpCache) {
                this.privateStorage(CacheStorage.Unlimited())
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }

            defaultRequest {
                header(HttpHeaders.ContentType, "application/json")
                header(HttpHeaders.Accept, "application/json")
                url(apiUrl)
            }
        }
    }

    single<HttpClient>(
        qualifier = named("bearer")
    ) {
        HttpClient().config {
            expectSuccess = true

            install(Logging) {
                level = LogLevel.ALL
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Log.d("HttpClient", message)
                    }
                }
            }

            install(HttpCache) {
                this.privateStorage(CacheStorage.Unlimited())
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }

            defaultRequest {
                header(HttpHeaders.ContentType, "application/json")
                header(HttpHeaders.Accept, "application/json")
                url(apiUrl)
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val tokenManager = get<TokenStorage>()

                        val (accessToken, refreshToken) = tokenManager.getAuthTokens()
                            ?: return@loadTokens null

                        BearerTokens(
                            accessToken = accessToken,
                            refreshToken = refreshToken
                        )
                    }

                    refreshTokens {
                        val tokenStorage = get<TokenStorage>()
                        val authApiClient = get<AuthApiClient>()

                        val result = tokenStorage.getAuthTokens()
                            ?.let { authApiClient.refreshToken(it) }
                            ?: return@refreshTokens null

                        result.fold(
                            onSuccess = {
                                tokenStorage.saveAuthTokens(it)

                                BearerTokens(
                                    accessToken = it.access,
                                    refreshToken = it.refresh
                                )
                            },
                            onError = { error ->
                                if (error is DataError.Unauthorized) {
                                    tokenStorage.deleteAll()
                                }

                                null
                            }
                        )
                    }
                }
            }
        }
    }

    singleOf(::AuthApiClient).bind<AuthApiClient>()
    single { UserApiClient(get(qualifier = named("bearer"))) }
}