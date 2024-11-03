package com.worldproger.mango.domain.repository

import com.worldproger.mango.app.main.profile_edit.AvatarData
import com.worldproger.mango.data.api.EditAvatarDTO
import com.worldproger.mango.data.api.UserApiClient
import com.worldproger.mango.data.mappers.toUserModel
import com.worldproger.mango.data.storage.TokenStorage
import com.worldproger.mango.domain.core.DataError
import com.worldproger.mango.domain.core.Result
import com.worldproger.mango.domain.model.UserModel

class UserRepository(
    private val userApiClient: UserApiClient,
    private val tokenStorage: TokenStorage,
) {
    suspend fun getUser(): Result<UserModel, DataError> {
        return userApiClient.getUser().map { it.toUserModel() }
    }

    suspend fun getUserCache(): Result<UserModel, DataError> {
        return userApiClient.getUserCache().map { it.toUserModel() }
    }

    suspend fun updateUser(
        name: String?,
        username: String?,
        birthday: String?,
        city: String?,
        instagram: String?,
        vk: String?,
        avatar: AvatarData?,
        status: String?,
    ): Result<UserModel, DataError> {
        return userApiClient.updateUser(
            name = name,
            username = username,
            birthday = birthday,
            city = city,
            instagram = instagram,
            vk = vk,
            avatar = avatar?.let { EditAvatarDTO(base64 = it.base64, filename = it.filename) },
            status = status,
        )
    }

    suspend fun logout() {
        tokenStorage.deleteAll()
    }
}