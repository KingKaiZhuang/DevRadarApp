package com.example.devradarapp.ui

// import androidx.compose.foundation.clickable // 移除點擊相關邏輯，所以不需要此 import
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

// ---------------- UI Components ----------------

// 1. 移除 onArticleClick 參數
@Composable
fun ExploreScreen() {
    val context = LocalContext.current // 取得 Context

    // 載入並解析 JSON 資料，使用 remember 確保只載入一次
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

            // Title Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Explore",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Filters Row
            item {
                FiltersRow()
                Spacer(modifier = Modifier.height(26.dp))
            }

            // Article Items
            // 使用 IThelpArticle 清單來疊代顯示
            items(articles) { item ->
                // 2. 移除 onClick 參數
                ExploreCard(item = item)
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

// 修改 ExploreCard
// 2. 移除 onClick 參數和 .clickable 修飾符
@Composable
fun ExploreCard(item: IThelpArticle) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            // .clickable { onClick(item) } // 移除可點擊
            .padding(20.dp)
    ) {
        // 標題 (Title)
        Text(
            text = item.title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 作者和日期資訊 (取代原有的 Source Row/Tags)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("作者: ${item.author.split('|')[0].trim()}", // 嘗試清理作者名稱
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

        // 描述 (Description)
        Text(
            text = item.desc,
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 統計數據 (取代 Tags)
        Row {
            Text("👍 ${item.like}", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(12.dp))
            Text("💬 ${item.comments}", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(12.dp))
            Text("👀 ${item.views}", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelSmall)
        }
    }
}

// ... (DifficultyBadge 和 Tag 元件保持不變，因為在 ExploreCard 中未被使用)

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
    val TAG = "JsonDataLoader" // 定義一個 Log 標籤

    val jsonString: String
    try {
        // 嘗試讀取 assets 資料夾中的檔案內容
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        Log.d(TAG, "步驟 1: 檔案 [$fileName] 讀取成功。字串長度: ${jsonString.length}")

    } catch (ioException: IOException) {
        // 讀取失敗（例如檔案不存在或路徑錯誤）
        Log.e(TAG, "步驟 1 失敗: 讀取 assets 檔案 [$fileName] 失敗！", ioException)
        return createDummyIThelpArticles().also {
            Log.d(TAG, "返回 [假資料] 清單。請檢查 assets 資料夾路徑是否正確。")
        }
    }

    return try {
        // 嘗試解析 JSON 字串
        // 這裡需要 kotlinx.serialization.decodeFromString
        val articles = Json.decodeFromString<List<IThelpArticle>>(jsonString)
        Log.d(TAG, "步驟 2: JSON 解析成功。文章數量: ${articles.size} 筆。")
        articles

    } catch (e: Exception) {
        // 解析失敗（例如 JSON 格式錯誤或資料模型不匹配）
        Log.e(TAG, "步驟 2 失敗: 解析 JSON 字串為 List<IThelpArticle> 失敗！", e)
        return createDummyIThelpArticles().also {
            Log.d(TAG, "返回 [假資料] 清單。請檢查 JSON 格式或 IThelpArticle 定義是否正確。")
        }
    }
}

fun createDummyIThelpArticles() : List<IThelpArticle> {
    return listOf(
        IThelpArticle(
            title = "💳 用 n8n 將信用卡消費資料寫入 Google Sheets (假資料)", // 加上 (假資料) 方便辨識
            desc = "這篇文章主要記錄如何用 n8n 把解析後的帳單資料自動寫入 Google Sheets...",
            url = "", author = "劉小貢 | 軟體工程師", date = "2025-11-11",
            like = "1", comments = "0", views = "1663"
        ),
        IThelpArticle(
            title = "【Compose】從零開始打造自訂主題和排版 (假資料)",
            desc = "深入探討 Material 3 的顏色系統、字體排版，以及如何用 CompositionLocal 傳遞主題。",
            url = "", author = "邦邦小幫手", date = "2025-11-15",
            like = "12", comments = "3", views = "2000"
        )
    )
}

// ---------------- Preview ----------------

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
// 3. 移除 Preview 中的參數
fun ExploreScreenPreview() {
    ExploreScreen()
}