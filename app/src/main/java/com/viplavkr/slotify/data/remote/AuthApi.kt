package com.viplavkr.slotify.data.remote

import com.viplavkr.slotify.data.model.FirebaseLoginRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/login")
    fun login(
        @Body request: FirebaseLoginRequest
    ): Call<Map<String, String>>
}
