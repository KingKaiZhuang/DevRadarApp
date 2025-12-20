package com.example.devradarapp.ui

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.example.devradarapp.utils.BrowserUtils
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
    
    // Find the article from the ViewModel's list
    val articles by viewModel.articles.collectAsState()
    // Decode URL if necessary, but here we assume simple matching or passed correctly
    // In a real app, URL encoding/decoding is crucial. For now we try straight match.
    val article = articles.find { it.url == articleUrl }

    // Load comments when entering the screen
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

    // State for replying
    var replyToComment by remember { mutableStateOf<Comment?>(null) }

    // Structure comments: Parent -> List<Reply>
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
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Article Content (Scrollable)
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item {
                    ArticleHeader(article)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { BrowserUtils.openArticleUrl(context, article.url) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("Read Full Article in Browser")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("Comments", style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(topLevelComments) { comment ->
                    val replies = repliesMap[comment.id] ?: emptyList()
                    CommentItem(
                        comment = comment,
                        replies = replies,
                        onReplyClick = { parent -> replyToComment = parent }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                if (comments.isEmpty()) {
                    item {
                        Text("No comments yet. Be the first!", color = Color.Gray, modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }

            // Comment Input Area
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
        
        // Reply Button
        TextButton(onClick = { onReplyClick(comment) }) {
            Text("Reply", color = Color(0xFF94A3B8), fontSize = 12.sp)
        }

        // Replies
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
