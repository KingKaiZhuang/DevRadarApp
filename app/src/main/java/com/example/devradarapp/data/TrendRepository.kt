package com.example.devradarapp.data

import com.example.devradarapp.network.RetrofitClient
import com.example.devradarapp.network.TrendKeyword

class TrendRepository {
    suspend fun getTrends(): List<TrendKeyword> {
        return try {
            RetrofitClient.instance.getTrends()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
