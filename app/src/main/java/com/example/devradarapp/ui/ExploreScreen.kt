package com.example.devradarapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.devradarapp.model.Article
import com.example.devradarapp.model.Notification

// ---------------- UI Components ----------------

@Composable
fun ExploreScreen(
    articles: List<Article>,
    favoriteUrls: Set<String> = emptySet(),
    onProfileClick: () -> Unit = {},
    onArticleClick: (String) -> Unit = {},
    onToggleFavorite: (Article) -> Unit = {},

    unreadNotificationCount: Int = 0,
    notifications: List<Notification> = emptyList(),
    onNotificationClick: (Notification) -> Unit = {},
    onRefreshNotifications: () -> Unit = {},
    onLoadMore: () -> Unit = {}
) {
    val context = LocalContext.current
    val background = Color(0xFF0F172A)
    
    var showNotificationDialog by remember { mutableStateOf(false) }

    if (showNotificationDialog) {
        NotificationDialog(
            notifications = notifications,
            onDismiss = { showNotificationDialog = false },
            onNotificationClick = { notification -> 
                 onNotificationClick(notification) 
                 // Optionally keep dialog open or close it
            }
        )
    }

    // State for date sorting
    var isNewestFirst by remember { mutableStateOf(true) }

    var selectedCategory by remember { mutableStateOf("All") }
    
    // Dynamically generate categories from the articles list
    val categories = remember(articles) {
        val allCategories = articles.mapNotNull { it.category }.distinct().sorted()
        listOf("All") + allCategories
    }

    val filteredArticles = remember(articles, selectedCategory, isNewestFirst) {
        var result = if (selectedCategory == "All") {
            articles
        } else {
            articles.filter { it.category == selectedCategory }
        }
        
        // Sort by date
        if (isNewestFirst) {
            result = result.sortedByDescending { it.date }
        } else {
            result = result.sortedBy { it.date }
        }
        result
    }

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
                        text = "資工 News",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Sort Button
                    IconButton(onClick = { isNewestFirst = !isNewestFirst }) {
                        Icon(
                            imageVector = if (isNewestFirst) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = if (isNewestFirst) "Newest First" else "Oldest First",
                            tint = Color.White
                        )
                    }

                    // Notification Icon with Badge
                    Box {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,

                            modifier = Modifier.clickable { 
                                showNotificationDialog = true 
                                onRefreshNotifications() 
                            }
                        )
                        if (unreadNotificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .clickable { onProfileClick() },
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

            // Filters
            item {
                FiltersRow(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelect = { selectedCategory = it }
                )
                Spacer(modifier = Modifier.height(26.dp))
            }

            // Articles
            itemsIndexed(filteredArticles) { index, item ->
                // Check if we need to load more
                if (index == filteredArticles.lastIndex) {
                    onLoadMore()
                }

                // 判斷此文章是否在收藏清單中
                val isFavorite = favoriteUrls.contains(item.url)

                ExploreCard(
                    item = item,
                    isFavorite = isFavorite, // 傳入狀態
                    onClick = { url -> onArticleClick(url) },
                    onFavoriteClick = { onToggleFavorite(item) } // 傳出事件
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}


@Composable
fun FiltersRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            val backgroundColor = if (isSelected) Color(0xFF3B82F6) else Color(0xFF1E293B)
            val textColor = if (isSelected) Color.White else Color(0xFF94A3B8)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(backgroundColor)
                    .clickable { onCategorySelect(category) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = category,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun ExploreCard(
    item: Article,
    isFavorite: Boolean,
    onClick: (String) -> Unit,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .clickable { onClick(item.url) }
            .padding(20.dp)
    ) {
        // 標題與收藏按鈕 Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = item.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // 收藏按鈕
            IconButton(
                onClick = { onFavoriteClick() }, // 這裡要阻斷父層點擊，IconButton 預設會阻斷
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFEB5757) else Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Category Badge
            val cat = item.category ?: "Uncategorized"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF3B82F6))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = cat, color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.width(12.dp))

            val authorName = item.author.split('|').firstOrNull()?.trim() ?: item.author
            Text("作者: $authorName", color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(12.dp))
            Text("日期: ${item.date}", color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelSmall)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.desc,
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Text("👍 ${item.like}", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(12.dp))
            Text("💬 ${item.comments}", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(12.dp))
            Text("👀 ${item.views}", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
fun ExploreScreenPreview() {
    ExploreScreen(articles = emptyList())
}
