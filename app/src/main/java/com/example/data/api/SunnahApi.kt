package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class ApiCollectionItem(
    val name: String,
    val hasBooks: Boolean = false,
    val hasChapters: Boolean = false,
    val collection: List<ApiLangTitle> = emptyList(),
    val totalHadith: Int = 0
)

@JsonClass(generateAdapter = true)
data class ApiLangTitle(
    val lang: String,
    val title: String
)

@JsonClass(generateAdapter = true)
data class ApiBookItem(
    val bookNumber: String,
    val book: List<ApiLangTitle> = emptyList(),
    val numberOfHadith: Int = 0
)

@JsonClass(generateAdapter = true)
data class ApiHadithText(
    val lang: String,
    val body: String,
    val chapterTitle: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiGrade(
    val grade: String,
    @param:Json(name = "graded_by") val gradedBy: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiHadithItem(
    val collection: String? = null,
    val bookNumber: String? = null,
    val hadithNumber: String? = null,
    val hadith: List<ApiHadithText> = emptyList(),
    val grades: List<ApiGrade> = emptyList(),
    val urn: Long? = null
)

@JsonClass(generateAdapter = true)
data class ApiHadithListResponse(
    val data: List<ApiHadithItem> = emptyList(),
    val total: Int = 0,
    val limit: Int = 50,
    val previous: Int? = null,
    val next: Int? = null
)

interface SunnahApiService {

    @GET("collections")
    suspend fun getCollections(
        @Header("x-api-key") apiKey: String?
    ): Response<List<ApiCollectionItem>>

    @GET("collections/{name}/books")
    suspend fun getBooks(
        @Path("name") collectionName: String,
        @Header("x-api-key") apiKey: String?
    ): Response<List<ApiBookItem>>

    @GET("collections/{name}/books/{bookNumber}/hadiths")
    suspend fun getHadiths(
        @Path("name") collectionName: String,
        @Path("bookNumber") bookNumber: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Header("x-api-key") apiKey: String?
    ): Response<ApiHadithListResponse>

    @GET("collections/{name}/hadiths/{hadithNumber}")
    suspend fun getHadithByNumber(
        @Path("name") collectionName: String,
        @Path("hadithNumber") hadithNumber: String,
        @Header("x-api-key") apiKey: String?
    ): Response<ApiHadithItem>
}

object SunnahApiClient {
    private const val BASE_URL = "https://api.sunnah.com/v1/"

    private val okHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    val service: SunnahApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SunnahApiService::class.java)
    }
}
