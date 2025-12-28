package com.example.devradarapp.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String,
    val userId: Int, // 改為 Int 以符合後端
    val message: String,
    val timestamp: Long,
    val isRead: Boolean,
    val articleUrl: String? = null
)
