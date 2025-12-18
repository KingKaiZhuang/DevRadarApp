package com.example.devradarapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devradarapp.network.TrendKeyword

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TrendScreen(
    keywords: List<TrendKeyword>,
    isLoading: Boolean,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trend Analysis") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Popular Keywords from Recent Articles",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        keywords.forEach { keyword ->
                            TrendTag(keyword)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrendTag(keyword: TrendKeyword) {
    // Simple sizing logic based on weight (assuming max around 50 for quick MVP scaling)
    // In a real app, we'd calculate counts relative to max count.
    // Here we just map values to font scale roughly.
    // Let's assume most values are 1-10, some higher.
    
    val fontSize = when {
        keyword.value > 20 -> 24.sp
        keyword.value > 10 -> 20.sp
        keyword.value > 5 -> 18.sp
        else -> 14.sp
    }

    val fontWeight = when {
        keyword.value > 10 -> FontWeight.Bold
        keyword.value > 5 -> FontWeight.SemiBold
        else -> FontWeight.Normal
    }
    
    val containerColor = when {
        keyword.value > 20 -> MaterialTheme.colorScheme.primaryContainer
        keyword.value > 10 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = "${keyword.text} (${keyword.value})",
            fontSize = fontSize,
            fontWeight = fontWeight,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
