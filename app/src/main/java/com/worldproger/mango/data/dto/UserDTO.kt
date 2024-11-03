package com.worldproger.mango.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoRequest(
    @SerialName("profile_data")
    val profileData: UserDTO
)

@Serializable
data class UserDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("avatar")
    val avatar: String?,
    @SerialName("avatars")
    val avatars: Avatars?,
    @SerialName("birthday")
    val birthday: String?,
    @SerialName("city")
    val city: String?,
    @SerialName("completed_task")
    val completedTask: Int,
    @SerialName("instagram")
    val instagram: String?,
    @SerialName("last")
    val last: String?,
    @SerialName("name")
    val name: String,
    @SerialName("online")
    val online: Boolean,
    @SerialName("phone")
    val phone: String,
    @SerialName("status")
    val status: String?,
    @SerialName("username")
    val username: String,
    @SerialName("vk")
    val vk: String?
)

@Serializable
data class Avatars(
    @SerialName("avatar")
    val avatar: String,
    @SerialName("bigAvatar")
    val bigAvatar: String,
    @SerialName("miniAvatar")
    val miniAvatar: String
)