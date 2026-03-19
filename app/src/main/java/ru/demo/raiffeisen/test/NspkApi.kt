package ru.demo.raiffeisen.test

import retrofit2.Response
import retrofit2.http.*

interface NspkApi {
    @GET("v2/sbp/members")
    suspend fun getMembers(
        @Header("X-CLIENT-ID") clientId: String,
        @Header("X-PLATFORM") platform: String,
        @Header("X-PAYLOAD") payload: String
    ): Response<MembersResponse>
}