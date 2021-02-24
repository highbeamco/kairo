package io.limberapp.client.exception

import io.ktor.http.HttpStatusCode

class LimberHttpClientException(
    val statusCode: HttpStatusCode,
    val errorMessage: String,
) : Exception("$statusCode response code.")