package com.example.devradarapp.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// ---------------------- 主畫面 ----------------------

@Composable
fun DailySummaryScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(top = 56.dp) // 頂部內距
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            // 今日 AI 摘要
            item {
                AIDailySummaryCard(
                    title = "每日 AI 摘要",
                    content = "大型語言模型現在能夠在最少的人工干預下生成可用於生產的程式碼。"
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 精選文章標題
            item {
                SectionTitle(text = "今日精選文章")
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 精選文章列表
            items(sampleArticles) { article ->
                ArticleCard(
                    title = article.title,
                    description = article.description,
                    tags = article.tags
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                RecommendSkillCard(skill = "Rust")
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Github 熱門專案
            item {
                SectionTitle(text = "GitHub 熱門專案")
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(sampleGithub) { repo ->
                GithubRepoCard(
                    repoName = repo.name,
                    description = repo.desc,
                    stars = repo.stars,
                    forks = repo.forks
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ---------------------- 元件：AI 摘要卡片 ----------------------

@Composable
fun AIDailySummaryCard(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .padding(20.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFF3B82F6),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// ---------------------- 元件：文章卡片 ----------------------

@Composable
fun ArticleCard(title: String, description: String, tags: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .padding(20.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            tags.forEach {
                ExploreTag(text = it)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

// ---------------------- 元件：技能推薦卡片 ----------------------

@Composable
fun RecommendSkillCard(skill: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = Color(0xFF3B82F6),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "您的技能地圖顯示您尚未探索",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "「$skill」。這是一個很好的起點。",
                color = Color(0xFF94A3B8),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// ---------------------- 元件：GitHub Repo 卡片 ----------------------

@Composable
fun GithubRepoCard(repoName: String, description: String, stars: String, forks: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .padding(20.dp)
    ) {
        Text(
            text = repoName,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Text(text = "☆ $stars", color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "⎈ $forks", color = Color(0xFF94A3B8))
        }
    }
}

// ---------------------- Tag 樣式 ----------------------

@Composable
fun ExploreTag(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(Color(0xFF334155))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodySmall)
    }
}


// ---------------------- 標題 ----------------------

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
    )
}

// ---------------------- Preview ----------------------

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
fun DailySummaryScreenPreview() {
    DailySummaryScreen()
}

// ---------------------- 假資料 ----------------------

data class ArticleSample(val title: String, val description: String, val tags: List<String>)
data class GithubSample(val name: String, val desc: String, val stars: String, val forks: String)

val sampleArticles = listOf(
    ArticleSample("精通異步 JavaScript", "深入探討 Promises、async/await 和事件循環，以編寫非阻塞程式碼。", listOf("JavaScript", "Async", "Web 開發")),
    ArticleSample("向量資料庫的崛起", "探索向量資料庫如何驅動下一代 AI 和搜尋應用程式。", listOf("資料庫", "AI/ML", "Embeddings")),
    ArticleSample("Docker 實用指南", "學習如何將您的應用程式容器化，以實現一致的環境和簡化的部署。", listOf("Docker", "DevOps", "容器"))
)

val sampleGithub = listOf(
    GithubSample("shadcn/ui", "設計精美的組件，可直接複製貼上使用。", "58.1k", "2.9k"),
    GithubSample("neovim/neovim", "高效能可自訂的 Vim 分支，適合任何工作流程。", "74.3k", "5.2k"),
    GithubSample("vlang/v", "一種簡單、安全、編譯型語言，用於開發可維護的軟體。", "35.2k", "2.2k")
)
