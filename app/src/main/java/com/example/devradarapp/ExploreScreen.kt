package com.example.devradarapp.ui

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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ExploreScreen(onArticleClick: (ExploreItem) -> Unit) {
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

            // Search Bar
            item {
                SearchBar()
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Category Tabs
            item {
                CategoryTabs()
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Filters Row
            item {
                FiltersRow()
                Spacer(modifier = Modifier.height(26.dp))
            }

            // Article Items
            items(sampleExploreItems) { item ->
                ExploreCard(item = item, onClick = { onArticleClick(item)})
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
fun SearchBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Search skills or keywords",
            color = Color(0xFF64748B),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CategoryTabs() {
    val tabs = listOf("All", "Frontend", "Backend", "DevOps", "AI")
    var selectedIndex by remember { mutableIntStateOf(0) }

    Row(modifier = Modifier.fillMaxWidth()) {
        tabs.forEachIndexed { index, text ->
            val selected = selectedIndex == index

            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(
                        if (selected) Color(0xFF3B82F6)
                        else Color(0xFF1E293B)
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = text,
                    color = if (selected) Color.White else Color(0xFFCBD5E1)
                )
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
fun ExploreCard(item: ExploreItem,onClick: (ExploreItem) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .clickable { onClick(item) }   // ← 新增可點擊
            .padding(20.dp)
    ) {

        // Source Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3B82F6))
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(item.source, color = Color(0xFFCBD5E1))

            Spacer(modifier = Modifier.weight(1f))

            DifficultyBadge(item.level)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = item.title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = item.desc,
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tags
        Row {
            item.tags.forEach {
                Tag(text = it)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun DifficultyBadge(level: String) {
    val bg = when (level) {
        "Beginner" -> Color(0xFF16A34A)
        "Intermediate" -> Color(0xFFEAB308)
        "Advanced" -> Color(0xFFDC2626)
        else -> Color(0xFF475569)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            level,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun Tag(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(Color(0xFF334155))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodySmall)
    }
}

// ---------------- Sample Data ----------------

data class ExploreItem(
    val source: String,
    val level: String,
    val title: String,
    val desc: String,
    val tags: List<String>
)

val sampleExploreItems = listOf(
    ExploreItem(
        "Medium",
        "Intermediate",
        "Mastering Asynchronous JavaScript in 2024",
        "A deep dive into promises, async/await, and modern patterns...",
        listOf("JavaScript", "Node.js")
    ),
    ExploreItem(
        "GitHub",
        "Beginner",
        "A Practical Guide to Docker for Beginners",
        "Learn the fundamentals of Docker, containers, and images...",
        listOf("Docker", "DevOps")
    ),
    ExploreItem(
        "Threads",
        "Advanced",
        "Optimizing React Native Performance",
        "Explore advanced techniques for profiling and debugging...",
        listOf("React Native", "Mobile")
    )
)

// ---------------- Preview ----------------

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
fun ExploreScreenPreview() {
    ExploreScreen(onArticleClick = {})
}
