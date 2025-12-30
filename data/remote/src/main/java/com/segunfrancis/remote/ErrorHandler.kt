package com.segunfrancis.remote

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.handleHttpExceptions(): String {
    printStackTrace()
    return when (this) {
        is HttpException -> {
            val errorMessage = when (code()) {
                400 -> "Bad Request - Missing or invalid parameters"
                401 -> "Unauthorized - Please login again"
                403 -> "Forbidden - You don't have permission for this action"
                404 -> "Not Found - The requested resource doesn't exist"
                429 -> "Too Many Requests - Please wait before trying again"
                500 -> "Internal Server Error - Please try again later"
                502 -> "Bad Gateway - Server connectivity issue"
                503 -> "Service Unavailable - Server maintenance in progress"
                504 -> "Gateway Timeout - Server took too long to respond"
                else -> "HTTP Error ${code()} - ${response()?.errorBody()?.string()}"
            }
            errorMessage
        }

        is SocketTimeoutException -> "Connection timeout - Please check your network"
        is UnknownHostException -> "No internet connection - Please check your network"

        is ConnectException -> "Cannot connect to server - Please try again later"
        is IOException -> "Network error - Please check your connection"
        else -> "Unexpected error: ${message ?: "Unknown error occurred"}"
    }
}
