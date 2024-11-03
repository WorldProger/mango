package com.worldproger.mango.data.mappers

import com.worldproger.mango.app.core.Constants
import com.worldproger.mango.data.dto.UserDTO
import com.worldproger.mango.domain.model.AvatarsModel
import com.worldproger.mango.domain.model.UserModel

fun UserDTO.toUserModel(): UserModel {
    return UserModel(
        id = id,
        avatars = avatars?.let { avatars ->
            AvatarsModel(
                avatar = attachBaseUrl(avatars.avatar)!!,
                bigAvatar = attachBaseUrl(avatars.bigAvatar)!!,
                miniAvatar = attachBaseUrl(avatars.miniAvatar)!!
            )
        },
        birthday = birthday,
        city = city,
        completedTask = completedTask,
        instagram = instagram,
        last = last,
        name = name,
        online = online,
        phone = phone,
        status = status,
        username = username,
        vk = vk
    )
}

private fun attachBaseUrl(url: String?): String? {
    return url?.let {
        if (it.startsWith("http")) {
            it
        } else {
            Constants.BASE_URL + it
        }
    }
}