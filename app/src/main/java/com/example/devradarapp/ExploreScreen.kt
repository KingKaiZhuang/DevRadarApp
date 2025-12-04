package com.example.devradarapp.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

// ---------------- Helper Function ----------------

/**
 * 使用 Custom Tabs 在 App 內開啟網頁
 */
fun openArticleUrl(context: Context, url: String) {
    if (url.isBlank()) return

    try {
        val builder = CustomTabsIntent.Builder()
        val params = androidx.browser.customtabs.CustomTabColorSchemeParams.Builder()
            .setToolbarColor(0xFF0F172A.toInt())
            .build()
        builder.setDefaultColorSchemeParams(params)

        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    } catch (e: Exception) {
        Log.e("Browser", "無法開啟網頁: $url", e)
    }
}

// ---------------- UI Components ----------------

@Composable
fun ExploreScreen(
    onProfileClick: () -> Unit = {} // 接收 MainActivity 傳來的導航事件
) {
    val context = LocalContext.current

    // 載入資料
    val articles: List<IThelpArticle> = remember {
        loadArticlesFromJson(context, "ithelp_hot.json")
    }

    val background = Color(0xFF0F172A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 20.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // --- Title Row ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "資工 News",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // 1. 通知按鈕
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier
                            .clickable { /* TODO */ }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // 2. Profile 按鈕 (串接導航)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .clickable { onProfileClick() }, // 觸發回呼
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Filters Row
            item {
                FiltersRow()
                Spacer(modifier = Modifier.height(26.dp))
            }

            // Article Items
            items(articles) { item ->
                ExploreCard(
                    item = item,
                    onClick = { url ->
                        openArticleUrl(context, url)
                    }
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
fun FiltersRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        DropdownFilter(text = "Latest")
        Spacer(modifier = Modifier.width(12.dp))
        DropdownFilter(text = "Beginner")

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Filters",
            color = Color(0xFF3B82F6),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 6.dp)
        )
    }
}

@Composable
fun DropdownFilter(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
fun ExploreCard(
    item: IThelpArticle,
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .clickable { onClick(item.url) }
            .padding(20.dp)
    ) {
        // 標題
        Text(
            text = item.title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 作者和日期
        Row(verticalAlignment = Alignment.CenterVertically) {
            val authorName = item.author.split('|').firstOrNull()?.trim() ?: item.author
            Text("作者: $authorName",
                color = Color(0xFF94A3B8),
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("日期: ${item.date}",
                color = Color(0xFF94A3B8),
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 描述
        Text(
            text = item.desc,
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 統計
        Row {
            Text("👍 ${item.like}", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(12.dp))
            Text("💬 ${item.comments}", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(12.dp))
            Text("👀 ${item.views}", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelSmall)
        }
    }
}

// ---------------- Data Model & Logic ----------------

@Serializable
data class IThelpArticle(
    val title: String,
    val desc: String,
    val url: String,
    val author: String,
    val date: String,
    val like: String,
    val comments: String,
    val views: String
)

fun loadArticlesFromJson(context: Context, fileName: String): List<IThelpArticle> {
    val TAG = "JsonDataLoader"
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        Log.e(TAG, "讀取 assets 檔案 [$fileName] 失敗！", ioException)
        return createDummyIThelpArticles()
    }

    return try {
        Json.decodeFromString<List<IThelpArticle>>(jsonString)
    } catch (e: Exception) {
        Log.e(TAG, "JSON 解析失敗！", e)
        return createDummyIThelpArticles()
    }
}

fun createDummyIThelpArticles() : List<IThelpArticle> {
    return listOf(
        IThelpArticle(
            title = "💳 用 n8n 將信用卡消費資料寫入 Google Sheets (假資料)",
            desc = "這篇文章主要記錄如何用 n8n 把解析後的帳單資料自動寫入 Google Sheets...",
            url = "https://ithelp.ithome.com.tw/",
            author = "劉小貢 | 軟體工程師", date = "2025-11-11",
            like = "1", comments = "0", views = "1663"
        ),
        IThelpArticle(
            title = "【Compose】從零開始打造自訂主題和排版 (假資料)",
            desc = "深入探討 Material 3 的顏色系統、字體排版，以及如何用 CompositionLocal 傳遞主題。",
            url = "https://ithelp.ithome.com.tw/",
            author = "邦邦小幫手", date = "2025-11-15",
            like = "12", comments = "3", views = "2000"
        )
    )
}

// ---------------- Preview ----------------

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
fun ExploreScreenPreview() {
    val context = LocalContext.current
    ExploreScreen(
        onProfileClick = {
            Toast.makeText(context, "點擊了 Profile", Toast.LENGTH_SHORT).show()
        }
    )
}