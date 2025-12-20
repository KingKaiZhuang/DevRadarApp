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
    @SerializedName("like_count") val like: String? = "0",
    @SerializedName("comment_count") val comments: String? = "0",
    @SerializedName("view_count") val views: String? = "0",
    @SerializedName("source") val source: String? = "Unknown",
    @SerializedName("is_bookmarked") val isBookmarked: Boolean = false,
    @SerializedName("category") val category: String? = "Uncategorized",

)
