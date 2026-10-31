package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class FreeApiGrade(
    val name: String? = null,
    val grade: String? = null
)

@JsonClass(generateAdapter = true)
data class FreeApiReference(
    val book: Int? = null,
    val hadith: Int? = null
)

@JsonClass(generateAdapter = true)
data class FreeApiMetadata(
    val name: String? = null,
    val section: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class FreeApiHadithItem(
    val hadithnumber: Double? = null,
    val arabicnumber: Double? = null,
    val text: String = "",
    val grades: List<FreeApiGrade> = emptyList(),
    val reference: FreeApiReference? = null
)

@JsonClass(generateAdapter = true)
data class FreeApiHadithResponse(
    val hadiths: List<FreeApiHadithItem>? = emptyList(),
    val metadata: FreeApiMetadata? = null
)

interface FreeHadithApiService {

    @GET("editions/{edition}/{hadithNumber}.json")
    suspend fun getHadith(
        @Path("edition") edition: String,
        @Path("hadithNumber") hadithNumber: String
    ): Response<FreeApiHadithResponse>
}

object FreeHadithApiClient {
    // Official jsDelivr CDN endpoint using tested release @1
    private const val BASE_URL = "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    val service: FreeHadithApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(FreeHadithApiService::class.java)
    }

    // Verified Arabic editions in Fawaz Ahmed dataset
    fun getArabicEdition(collection: String): String = when (collection.lowercase()) {
        "bukhari" -> "ara-bukhari"
        "muslim" -> "ara-muslim"
        else -> "ara-$collection"
    }

    // Verified English editions in Fawaz Ahmed dataset
    fun getEnglishEdition(collection: String): String? = when (collection.lowercase()) {
        "bukhari" -> "eng-bukhari"
        "muslim" -> "eng-muslim"
        else -> null
    }

    // Verified Urdu editions in Fawaz Ahmed dataset
    fun getUrduEdition(collection: String): String? = when (collection.lowercase()) {
        "bukhari" -> "urd-bukhari"
        "muslim" -> "urd-muslim"
        else -> null
    }
}
