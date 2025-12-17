package com.example.devradarapp.network

import com.example.devradarapp.model.Article
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("articles")
    suspend fun getArticles(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 20
    ): List<Article>

    // Future: Add trigger scrape or bookmark endpoints here if needed
}
