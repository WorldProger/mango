package com.worldproger.mango.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tokens(
   @SerialName("access") val access: String,
   @SerialName("refresh") val refresh: String,
)