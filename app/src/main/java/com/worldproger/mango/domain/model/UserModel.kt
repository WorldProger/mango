package com.worldproger.mango.domain.model

data class UserModel(
    val id: Int,
    val avatars: AvatarsModel?,
    val birthday: String?,
    val city: String?,
    val completedTask: Int,
    val instagram: String?,
    val last: String?,
    val name: String,
    val online: Boolean,
    val phone: String,
    val status: String?,
    val username: String,
    val vk: String?
)

data class AvatarsModel(
    val avatar: String,
    val bigAvatar: String,
    val miniAvatar: String
)