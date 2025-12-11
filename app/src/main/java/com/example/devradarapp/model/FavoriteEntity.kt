package com.example.devradarapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    // 外鍵約束：當使用者被刪除時，其收藏也會被刪除 (CASCADE)
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    // 索引：確保同一個使用者對同一篇文章只能收藏一次，加快查詢速度
    indices = [Index(value = ["userId", "articleUrl"], unique = true)]
)
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val articleUrl: String,
    val title: String, // 儲存標題以便離線顯示列表
    val author: String,
    val date: String
)
