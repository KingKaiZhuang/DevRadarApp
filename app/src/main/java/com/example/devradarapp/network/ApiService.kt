package com.example.devradarapp.network

import com.example.devradarapp.model.Article
import com.example.devradarapp.model.Comment
import com.example.devradarapp.model.Notification
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.Part
import okhttp3.MultipartBody
import kotlinx.serialization.Serializable

@Serializable
data class TrendKeyword(
    val text: String,
    val value: Int
)

@Serializable
data class UploadResponse(
    val url: String
)

interface ApiService {
    @GET("articles")
    suspend fun getArticles(
        @Query("skip") skip: Int, 
        @Query("limit") limit: Int,
        @Query("category") category: String? = null
    ): List<Article>

    @GET("comments")
    suspend fun getComments(@Query("articleUrl") articleUrl: String): List<Comment>

    @POST("comments")
    suspend fun addComment(@Body comment: Comment): Comment

    @GET("notifications")
    suspend fun getNotifications(@Query("userId") userId: Int): List<Notification>

    @POST("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: String)

    @GET("trends")
    suspend fun getTrends(): List<TrendKeyword>

    @Multipart
    @POST("users/{userId}/avatar")
    suspend fun uploadAvatar(
        @Path("userId") userId: Int,
        @Part file: MultipartBody.Part
    ): UploadResponse
}
