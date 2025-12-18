package com.example.devradarapp.network

import com.example.devradarapp.model.Article
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("articles")
    suspend fun getArticles(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 20
    ): List<Article>

    // Comment Endpoints
    @GET("comments")
    suspend fun getComments(@Query("articleUrl") articleUrl: String): List<com.example.devradarapp.model.Comment>

    @POST("comments")
    suspend fun addComment(@Body comment: com.example.devradarapp.model.Comment): com.example.devradarapp.model.Comment

    // Notification Endpoints
    @GET("notifications")
    suspend fun getNotifications(@Query("userId") userId: Int): List<com.example.devradarapp.model.Notification>

    @POST("notifications/{id}/read")
    suspend fun markNotificationRead(@retrofit2.http.Path("id") id: String): Map<String, String>
}
