package com.worldproger.mango.data.api

import android.util.Log
import com.worldproger.mango.data.dto.ErrorMessage
import com.worldproger.mango.domain.core.DataError
import com.worldproger.mango.domain.core.Result
import com.worldproger.mango.domain.core.Result.Companion.fold
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.JsonConvertException
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Result<T, DataError> {
    return withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiCall()
            Result.Success(response)
        } catch (e: Exception) {
            val dataError = when (e) {
                is IOException -> DataError.NoInternet()
                is TimeoutCancellationException -> DataError.RequestTimeout()
                is SerializationException, is JsonConvertException -> DataError.Serialization(e.message)
                is ResponseException -> {

                    val errorMessage = parseErrorMessage(e.response.bodyAsText())

                    when (e.response.status.value) {
                        400 -> DataError.BadRequest(message = errorMessage)
                        401 -> DataError.Unauthorized(message = errorMessage)
                        403 -> DataError.Forbidden(message = errorMessage)
                        404 -> DataError.NotFound(message = errorMessage)
                        413 -> DataError.PayloadTooLarge(message = errorMessage)
                        429 -> DataError.TooManyRequests(message = errorMessage)
                        in 500..599 -> DataError.ServerError(message = errorMessage)
                        else -> DataError.BadResponse(message = errorMessage)
                    }
                }

                else -> {
                    Log.e("API", "Unknown error in safeApiCall: ${e.message}")
                    DataError.Unknown(e.message ?: "Unknown error ${e::class.simpleName}")
                }
            }

            Result.Error(dataError)
        }
    }
}

suspend fun <T, R> safeApiCall(
    apiCall: suspend () -> T,
    transform: suspend (T) -> R
): Result<R, DataError> {
    return safeApiCall(apiCall).fold(
        onSuccess = { data ->
            try {
                Result.Success(transform(data))
            } catch (e: Exception) {
                Result.Error(DataError.Serialization("Failed to transform response ${e.message}"))
            }
        },
        onError = { error ->
            Result.Error(error)
        }
    )
}

private fun parseErrorMessage(responseBody: String): String? {
    return try {
        val errorMessage = json.decodeFromString<ErrorMessage>(responseBody)
        errorMessage.message
    } catch (e: Exception) {
        null // Возвращаем null, если декодирование не удалось
    }
}