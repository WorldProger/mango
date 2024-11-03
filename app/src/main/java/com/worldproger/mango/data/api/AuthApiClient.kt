package com.worldproger.mango.data.api

import com.worldproger.mango.data.dto.Tokens
import com.worldproger.mango.domain.core.DataError
import com.worldproger.mango.domain.core.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AuthApiClient(
    private val client: HttpClient,
) {
    suspend fun refreshToken(tokens: Tokens): Result<Tokens, DataError> {
        return safeApiCall {
            client.post("users/refresh-token/") {
                setBody(
                    RefreshTokenRequest(
                        refreshToken = tokens.refresh,
                    )
                )
            }.body<RefreshTokenResponse>()
        }.map {
            Tokens(
                access = it.accessToken,
                refresh = it.refreshToken,
            )
        }
    }

    suspend fun sendPhone(phone: String): Result<Unit, DataError> {
        return safeApiCall {
            client.post("users/send-auth-code/") {
                setBody(SendPhoneRequest(phone))
            }
        }
    }

    suspend fun verifyCode(phone: String, code: String): Result<VerifyCodeResponse, DataError> {
        return safeApiCall {
            client.post("users/check-auth-code/") {
                setBody(
                    VerifyPhoneRequest(
                        phone = phone, code = code
                    )
                )
            }.body<VerifyCodeResponse>()
        }
    }

    suspend fun register(
        phone: String, name: String, username: String
    ): Result<RegisterResponse, DataError> {
        return safeApiCall {
            client.post("users/register/") {
                setBody(
                    RegisterRequest(
                        phone = phone, name = name, username = username
                    )
                )
            }.body<RegisterResponse>()
        }
    }
}

@Serializable
data class RefreshTokenRequest(
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class RefreshTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class SendPhoneRequest(
    @SerialName("phone") val phone: String,
)

@Serializable
data class VerifyPhoneRequest(
    @SerialName("phone") val phone: String,
    @SerialName("code") val code: String,
)

@Serializable
data class VerifyCodeResponse(
    @SerialName("is_user_exists") val isUserExists: Boolean,
    @SerialName("access_token") val accessToken: String?,
    @SerialName("refresh_token") val refreshToken: String?,
    @SerialName("user_id") val userId: Int?
)

@Serializable
data class RegisterRequest(
    @SerialName("name") val name: String,
    @SerialName("phone") val phone: String,
    @SerialName("username") val username: String
)

@Serializable
data class RegisterResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user_id") val userId: Int
)