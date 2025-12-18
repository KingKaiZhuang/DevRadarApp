package com.example.devradarapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String,
    val articleUrl: String,
    val userId: Int,
    val userName: String,
    val content: String,
    val timestamp: Long,
    val parentId: String? = null
)
