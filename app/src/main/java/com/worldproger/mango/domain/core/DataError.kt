package com.worldproger.mango.domain.core

sealed class DataError(open val message: String? = null) {
    data class TooManyRequests(override val message: String? = null) : DataError(message)
    data class RequestTimeout(override val message: String? = null) : DataError(message)
    data class NoInternet(override val message: String? = null) : DataError(message)
    data class PayloadTooLarge(override val message: String? = null) : DataError(message)
    data class ServerError(override val message: String? = null) : DataError(message)
    data class Serialization(override val message: String? = null) : DataError(message)
    data class NotFound(override val message: String? = null) : DataError(message)
    data class Forbidden(override val message: String? = null) : DataError(message)
    data class Unauthorized(override val message: String? = null) : DataError(message)
    data class BadRequest(override val message: String? = null) : DataError(message)
    data class BadResponse(override val message: String? = null) : DataError(message)
    data class Unknown(override val message: String) : DataError(message)
}