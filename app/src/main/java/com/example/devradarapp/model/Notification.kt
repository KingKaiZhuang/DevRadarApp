package com.example.devradarapp.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String,
    val userId: Int, // Changed to Int to match backend
    val message: String,
    val timestamp: Long,
    val isRead: Boolean,
    val articleUrl: String? = null
)
