package com.example.devradarapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devradarapp.data.UserEntity

// 定義顏色
val DarkBg = Color(0xFF12141C)
val CardBg = Color(0xFF1C1F2A)
val AccentBlue = Color(0xFF2F80ED)
val TextWhite = Color(0xFFFFFFFF)
val TextGrey = Color(0xFF8F9BB3)
val LogoutRed = Color(0xFFEB5757)
val DividerColor = Color(0xFF2C303E)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    currentUser: UserEntity? = null,
    onClose: () -> Unit,
    onLogout: () -> Unit,
    onFavoritesClick: () -> Unit = {} // 新增：點擊收藏的回呼
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            ProfileTopBar(onClose = onClose)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            // 1. 個人檔案
            ProfileSection(user = currentUser)

            Spacer(modifier = Modifier.height(32.dp))

            // 新增：收藏夾按鈕 (只有登入後才顯示，或訪客點擊後提示登入)
            if (currentUser != null) {
                MenuButton(
                    title = "My Favorites",
                    icon = Icons.Default.Favorite,
                    onClick = onFavoritesClick
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // 2. Areas of Interest
            Text(
                text = "Areas of Interest",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            // ... (略，保持原有內容)
            Text(
                text = "Select topics to personalize your article feed.",
                color = TextGrey,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val interests = listOf(
                    "AI/ML" to true, "Frontend" to false, "DevOps" to false, "JavaScript" to true,
                    "Python" to false, "Cloud Computing" to false, "Cybersecurity" to true
                )

                interests.forEach { (name, isSelected) ->
                    InterestChip(text = name, isSelected = isSelected)
                }
                AddMoreChip()
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Data Sources
            Text(
                text = "Data Sources",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            DataSourceCard()

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Logout 按鈕
            LogoutButton(onClick = onLogout)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- 組件拆分 ---

@Composable
fun ProfileTopBar(onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = TextWhite,
            modifier = Modifier
                .size(24.dp)
                .clickable { onClose() }
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Settings",
            color = TextWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.size(24.dp))
    }
}

@Composable
fun ProfileSection(user: UserEntity?) {
    val displayName = user?.name ?: "Guest User"
    val displayEmail = user?.email ?: "guest@devradar.com"
    val displayInitials = user?.initials ?: "G"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0C4A8)),
            contentAlignment = Alignment.Center
        ) {
            Text(displayInitials, color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = displayName,
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = displayEmail,
                color = TextGrey,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun InterestChip(text: String, isSelected: Boolean) {
    val bgColor = if (isSelected) AccentBlue else CardBg
    val textColor = TextWhite

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AddMoreChip() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF3E4352), RoundedCornerShape(8.dp))
            .clickable { }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = TextGrey,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Add more",
            color = TextGrey,
            fontSize = 14.sp
        )
    }
}

// 新增：通用的選單按鈕
@Composable
fun MenuButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CardBg)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextWhite
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = TextWhite,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = TextGrey,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun DataSourceCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
    ) {
        DataSourceItem("Medium", true)
        HorizontalDivider(color = DividerColor, thickness = 1.dp)
        DataSourceItem("Thread", true)
        HorizontalDivider(color = DividerColor, thickness = 1.dp)
        DataSourceItem("GitHub Trending", true)
        HorizontalDivider(color = DividerColor, thickness = 1.dp)
        DataSourceItem("Reddit", false)
    }
}

@Composable
fun DataSourceItem(title: String, initialChecked: Boolean) {
    var checked by remember { mutableStateOf(initialChecked) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = TextWhite,
            fontSize = 16.sp
        )
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextWhite,
                checkedTrackColor = AccentBlue,
                uncheckedThumbColor = TextGrey,
                uncheckedTrackColor = Color(0xFF3E4352),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CardBg)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = null,
            tint = LogoutRed
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Logout",
            color = LogoutRed,
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfile() {
    ProfileScreen(onClose = {}, onLogout = {})
}