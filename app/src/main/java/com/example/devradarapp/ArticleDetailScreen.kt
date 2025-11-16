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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.InsertLink
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ArticleDetailScreen(
    onBack: () -> Unit = {},
    onOpenSource: () -> Unit = {},
    onFavorite: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            TopBar(onBack, onFavorite)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {

                item {
                    ArticleTitleBlock()
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    TagRow()
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    DetailSectionTitle("AI 評估")
                    Spacer(modifier = Modifier.height(12.dp))
                    AIEvaluationCard()
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    DetailSectionTitle("AI 摘要")
                    Spacer(modifier = Modifier.height(12.dp))
                    AISummary()
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    SectionTitle("必看重點")
                    Spacer(modifier = Modifier.height(12.dp))
                    KeyPointsList()
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            BottomButtons(
                onOpenSource = onOpenSource,
                onFavorite = onFavorite
            )
        }
    }
}

@Composable
fun TopBar(onBack: () -> Unit, onFavorite: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "文章精華",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onFavorite) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun ArticleTitleBlock() {
    Column {
        Text(
            text = "精通 React 中的狀態管理：Zustand 實戰",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "作者 John Doe 於 Smashing Magazine - 2023年10月26日",
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun TagRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("React", "Zustand", "狀態管理", "前端開發").forEach {
            ArticleTag(it)
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
fun ArticleTag(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = Color(0xFFCBD5E1))
    }
}

@Composable
fun DetailSectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun AIEvaluationCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .padding(18.dp)
    ) {

        EvaluationRow("文章難度", "中級")
        Spacer(modifier = Modifier.height(12.dp))
        EvaluationRow("實務價值", "高")
        Spacer(modifier = Modifier.height(12.dp))
        EvaluationRow("前置技能", "JavaScript ES6")
    }
}

@Composable
fun EvaluationRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFFCBD5E1),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AISummary() {
    Text(
        text = "Zustand 透過運用 hooks 簡化了 React 的狀態管理。它提供了一個極簡的 API，避免了其他函式庫中常見的樣板程式碼。本⽂探討如何設定 store、建立 actions，並將組件連接起來...",
        color = Color(0xFFCBD5E1),
        style = MaterialTheme.typography.bodyMedium
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = "閱讀更多",
        color = Color(0xFF3B82F6),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun KeyPointsList() {
    Column {
        KeyPointItem(1, "與 Redux 相比，Zustand 大幅減少樣板程式碼，使狀態管理更簡潔易讀。")
        KeyPointItem(2, "它與 React hooks 無縫整合，為管理全域狀態提供現代化且直觀的開發體驗。")
        KeyPointItem(3, "非同步 actions 無需額外中介軟體即可直接處理，簡化了資料獲取和副作用管理。")
    }
}

@Composable
fun KeyPointItem(number: Int, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF3B82F6)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            color = Color(0xFFCBD5E1),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun BottomButtons(onOpenSource: () -> Unit, onFavorite: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Button(
            onClick = onOpenSource,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
        ) {
            Icon(
                imageVector = Icons.Default.InsertLink,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "查看原文", color = Color.White)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onFavorite,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "收藏文章", color = Color.White)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
fun ArticleDetailPreview() {
    ArticleDetailScreen()
}
