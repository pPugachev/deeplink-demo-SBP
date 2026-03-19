package ru.demo.raiffeisen.test

import retrofit2.Response
import retrofit2.http.*

interface RaifApi {
    @POST("api/sbp/v2/qr/register")
    @Headers("Content-Type: application/json")
    suspend fun registerQr(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}

data class RegisterRequest(
    val publicId: String,
    val amount: String,
    val order: String,
    val origin: String = "PAYMENT"
)

data class RegisterResponse(
    val payload: String
)