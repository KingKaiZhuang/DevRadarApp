package com.example.devradarapp.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val id: Int? = null,
    val title: String,
    val desc: String,
    val url: String,
    val author: String,
    val date: String,
    @SerializedName("like_count") val like: String,
    @SerializedName("comment_count") val comments: String,
    @SerializedName("view_count") val views: String,
    @SerializedName("is_bookmarked") val isBookmarked: Boolean = false,
    @SerializedName("category") val category: String? = "Uncategorized"
)
