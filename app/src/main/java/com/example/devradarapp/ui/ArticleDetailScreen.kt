package com.example.devradarapp.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devradarapp.model.Article
import com.example.devradarapp.model.Comment
import com.example.devradarapp.model.UserEntity

import com.example.devradarapp.viewmodel.ArticleViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    articleUrl: String,
    viewModel: ArticleViewModel,
    currentUser: UserEntity?,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    
    // 從 ViewModel 列表中尋找文章
    val articles by viewModel.articles.collectAsState()
    // 尋找文章
    val article = articles.find { it.url == articleUrl }

    // 進入頁面時載入留言
    LaunchedEffect(articleUrl) {
        viewModel.loadComments(articleUrl)
    }

    val comments by viewModel.currentComments.collectAsState()

    if (article == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Article not found", color = Color.White)
            Button(onClick = onBackClick) { Text("Back") }
        }
        return
    }

    // 回覆狀態
    var replyToComment by remember { mutableStateOf<Comment?>(null) }
    
    // AI 對話框狀態
    var showAiDialog by remember { mutableStateOf(false) }
    var aiPrompt by remember { mutableStateOf("") }
    val aiResponse by viewModel.aiResponse.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    // 留言結構：父留言 -> List<回覆>
    val topLevelComments = remember(comments) { comments.filter { it.parentId == null }.sortedByDescending { it.timestamp } }
    val repliesMap = remember(comments) { comments.filter { it.parentId != null }.groupBy { it.parentId } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Article Details", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAiDialog = true },
                icon = { Icon(Icons.Default.Star, contentDescription = "AI Assistant") },
                text = { Text("AI Assistant") },
                containerColor = Color(0xFF8B5CF6), // 紫色代表 AI 風格
                contentColor = Color.White
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                // 1. 文章標題
                item {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // 2. 文章資訊
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${article.source} • ${article.date ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = article.author ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // 3. 文章內容
                item {
                    Text(
                        text = article.desc ?: "No content available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 24.sp
                    )
                }

                // 4. 連結按鈕
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(article.url))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text(text = "Read Full Article in Browser")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 5. 留言標題與輸入框
                item {
                    Text(
                        text = "Comments",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    CommentInputArea(
                        replyTo = replyToComment,
                        onCancelReply = { replyToComment = null },
                        onSend = { text ->
                            if (currentUser != null) {
                                viewModel.addComment(article.url, text, currentUser, replyToComment?.id)
                                replyToComment = null
                            } else {
                                Toast.makeText(context, "Please login to comment", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 6. 留言列表 (項目)
                items(topLevelComments) { comment ->
                    val replies = repliesMap[comment.id] ?: emptyList()
                    CommentItem(
                        comment = comment,
                        replies = replies,
                        onReplyClick = { parent -> replyToComment = parent }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (showAiDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showAiDialog = false 
                    viewModel.clearAiResponse()
                },
                title = { Text("✨ AI Assistant") },
                text = {
                    Column {
                        if (aiResponse == null && !isAiLoading) {
                            Text("Ask something about this article:")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = aiPrompt,
                                onValueChange = { aiPrompt = it },
                                label = { Text("Question") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            // 建議
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Button(onClick = { aiPrompt = "Summarize this article" }, modifier = Modifier.padding(end = 4.dp)) {
                                    Text("Summarize", fontSize = 10.sp)
                                }
                                Button(onClick = { aiPrompt = "Explain basic concepts" }) {
                                    Text("Explain", fontSize = 10.sp)
                                }
                            }
                        } else if (isAiLoading) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                           // 顯示回應
                           Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                               Text(aiResponse ?: "", style = MaterialTheme.typography.bodyMedium)
                           }
                        }
                    }
                },
                confirmButton = {
                    if (aiResponse == null && !isAiLoading) {
                        Button(onClick = {
                            if (aiPrompt.isNotBlank()) {
                                viewModel.askAi(aiPrompt, article.desc ?: article.title) // 使用描述或回退到標題
                            }
                        }) {
                            Text("Ask AI")
                        }
                    } else {
                         Button(onClick = { 
                             showAiDialog = false
                             viewModel.clearAiResponse()
                             aiPrompt = "" // 重置提示
                         }) {
                             Text("Close")
                         }
                    }
                },
                dismissButton = {
                    if (aiResponse == null && !isAiLoading) {
                        TextButton(onClick = { showAiDialog = false }) {
                            Text("Cancel")
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun ArticleHeader(article: Article) {
    Column {
        Text(
            text = article.title,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "By ${article.author}", color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = article.date, color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = article.desc,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFCBD5E1)
        )
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    replies: List<Comment> = emptyList(),
    onReplyClick: (Comment) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = comment.userName, color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            val date = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date(comment.timestamp))
            Text(text = date, color = Color.Gray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = comment.content, color = Color.White)
        
        // 回覆按鈕
        TextButton(onClick = { onReplyClick(comment) }) {
            Text("Reply", color = Color(0xFF94A3B8), fontSize = 12.sp)
        }

        // 回覆列表
        if (replies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(start = 16.dp)) {
                replies.forEach { reply ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = reply.userName, color = Color(0xFF60A5FA), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = reply.content, color = Color(0xFFE2E8F0), fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun CommentInputArea(
    replyTo: Comment?,
    onCancelReply: () -> Unit,
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        if (replyTo != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF334155))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Replying to ${replyTo.userName}",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onCancelReply, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.LightGray)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(if (replyTo != null) "Write a reply..." else "Add a comment...", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF3B82F6),
                    unfocusedBorderColor = Color(0xFF94A3B8),
                    cursorColor = Color(0xFF3B82F6)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (text.isNotBlank()) {
                        onSend(text)
                        text = ""
                        keyboardController?.hide()
                    }
                }),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSend(text)
                        text = ""
                        keyboardController?.hide()
                    }
                },
                modifier = Modifier.background(Color(0xFF3B82F6), androidx.compose.foundation.shape.CircleShape)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}
