package com.example.devradarapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val title: String,
    val desc: String,
    val url: String,
    val author: String,
    val date: String,
    val like: String,
    val comments: String,
    val views: String
)
