package ru.demo.raiffeisen.test

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL_NSPK = "https://widget.cbrpay.ru/"
    private const val BASE_URL_RAIF = "https://pay.raif.ru/"

    // Создаём Gson с включённым lenient режимом и поддержкой BOM
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    // Логирующий перехватчик
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: NspkApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_NSPK)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(NspkApi::class.java)
    }

    val raifApi: RaifApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_RAIF)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(RaifApi::class.java)
    }
}