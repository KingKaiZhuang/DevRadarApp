
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun OnboardingScreen(
    onGoogleClick: () -> Unit = {},
    onGithubClick: () -> Unit = {},
    onGuestClick: () -> Unit = {} // 新增訪客 Callback
) {
    val iconSize = 64.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(80.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF3B82F6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = "輕鬆保持領先",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Features
            FeatureItem(
                icon = Icons.Default.CalendarMonth,
                title = "每日技術摘要",
                desc = "獲取精選的每日最新技術文章摘要。"
            )
            FeatureItem(
                icon = Icons.Default.TrackChanges,
                title = "個人技能雷達",
                desc = "根據您的閱讀內容追蹤您的技能成長。"
            )
            FeatureItem(
                icon = Icons.Default.Folder,
                title = "自動文章整理",
                desc = "輕鬆自動儲存和分類文章。"
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Google Login
            LoginButton(
                text = "使用 Google 繼續",
                icon = Icons.Default.Login,
                background = Color(0xFFE2E8F0),
                textColor = Color(0xFF0F172A),
                onClick = onGoogleClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // GitHub Login
            LoginButton(
                text = "使用 GitHub 繼續",
                icon = Icons.Default.AccountBox,
                background = Color(0xFF1E293B),
                textColor = Color.White,
                onClick = onGithubClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // -----------------------------
            // 新增：訪客登入按鈕
            // -----------------------------
            LoginButton(
                text = "以訪客身分繼續",
                icon = Icons.Default.Person,
                background = Color(0xFF334155),
                textColor = Color.White,
                onClick = onGuestClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "繼續即表示您同意我們的服務條款和隱私政策。",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF94A3B8)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}



@Composable
fun FeatureItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF3B82F6),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
            )
        }
    }
}


@Composable
fun LoginButton(
    text: String,
    icon: ImageVector,
    background: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text)
    }
}
