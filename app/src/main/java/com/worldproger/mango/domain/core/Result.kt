package com.worldproger.mango.domain.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


sealed interface Result<out D, out E> {
    data class Success<out D, out E>(val data: D) : Result<D, E>
    data class Error<out D, out E>(val error: E) : Result<D, E>

    fun <T> map(transform: (D) -> T): Result<T, E> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(error)
    }

    suspend fun <T> suspendMap(transform: suspend (D) -> T): Result<T, E> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(error)
    }

    companion object {

        @OptIn(ExperimentalContracts::class)
        inline fun <R, T, F> Result<T, F>.fold(
            onSuccess: (value: T) -> R,
            onError: (exception: F) -> R
        ): R {

            contract {
                callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
                callsInPlace(onError, InvocationKind.AT_MOST_ONCE)
            }

            return when (val error = this.errorOrNull()) {
                null -> onSuccess(this.getOrThrow())
                else -> onError(error)
            }
        }

        inline fun <D, E> Result<D, E>.onSuccess(action: (data: D) -> Unit): Result<D, E> {
            if (this is Success) action(data)
            return this
        }

        inline fun <D, E> Result<D, E>.onError(action: (error: E) -> Unit): Result<D, E> {
            if (this is Error) action(error)
            return this
        }
    }

    fun errorOrNull(): E? = when (this) {
        is Success -> null
        is Error -> error
    }

    fun errorOrThrow(): E = when (this) {
        is Success -> throw Throwable("Result is not an Error - $this")
        is Error -> error
    }

    fun getOrNull(): D? = when (this) {
        is Success -> data
        is Error -> null
    }

    fun getOrThrow(): D = when (this) {
        is Success -> data
        is Error -> throw Throwable("Result is Error - $this")
    }

    fun isSuccess(): Boolean = when (this) {
        is Success -> true
        is Error -> false
    }

    fun isError(): Boolean = when (this) {
        is Success -> false
        is Error -> true
    }
}