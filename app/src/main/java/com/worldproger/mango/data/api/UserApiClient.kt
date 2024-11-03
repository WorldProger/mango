package com.worldproger.mango.data.api

import com.worldproger.mango.data.dto.UserDTO
import com.worldproger.mango.data.dto.UserInfoRequest
import com.worldproger.mango.data.mappers.toUserModel
import com.worldproger.mango.domain.core.DataError
import com.worldproger.mango.domain.core.Result
import com.worldproger.mango.domain.model.UserModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class UserApiClient(
    private val client: HttpClient,
) {
    suspend fun getUser(): Result<UserDTO, DataError> {
        return safeApiCall {
            client.get("users/me/").body<UserInfoRequest>().profileData
        }
    }

    suspend fun getUserCache(): Result<UserDTO, DataError> {
        val result = safeApiCall {
            client.get("users/me/") {
                headers.append(
                    HttpHeaders.CacheControl,
                    "only-if-cached, max-stale=${Int.MAX_VALUE}"
                )
            }.body<UserInfoRequest>().profileData
        }

        if (result is Result.Error) {
            return getUser()
        }

        return result
    }

    suspend fun updateUser(
        name: String?,
        username: String?,
        birthday: String?,
        city: String?,
        instagram: String?,
        vk: String?,
        status: String?,
        avatar: EditAvatarDTO?,
    ): Result<UserModel, DataError> {
        safeApiCall {
            client.put("users/me/") {
                setBody(
                    EditUserRequest(
                        birthday = birthday,
                        city = city,
                        instagram = instagram,
                        name = name,
                        status = status,
                        username = username,
                        vk = vk,
                        avatar = avatar,
                    )
                )
            }
        }

        return getUser().map { it.toUserModel() }
    }

}

@Serializable
data class EditUserRequest(
    @SerialName("avatar")
    val avatar: EditAvatarDTO? = null,
    @SerialName("birthday")
    val birthday: String? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("instagram")
    val instagram: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("username")
    val username: String? = null,
    @SerialName("vk")
    val vk: String? = null
)

@Serializable
data class EditAvatarDTO(
    @SerialName("base_64")
    val base64: String,
    @SerialName("filename")
    val filename: String
)

