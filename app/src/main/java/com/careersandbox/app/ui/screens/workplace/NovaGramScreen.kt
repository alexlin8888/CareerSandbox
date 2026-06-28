package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite as FilledFavorite
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import com.careersandbox.app.data.mock.WorkplaceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R

/* =====================================================================
   NovaGram —— 你的個人檔 + 動態牆(嵌真實照片)
   封面 profile_cover;三篇貼文用真實照片 feed_1(夜市) / feed_2(辦公室) / feed_3(爬山)。
   與 Day4 的小芳限動區隔(那是別人的 story,這是你的個人頁)。
   ===================================================================== */

private data class GramPost(val img: Int, val caption: String, val likes: String)

@Composable
fun NovaGramScreen(navController: NavHostController) {
    val scroll = rememberScrollState()
    LaunchedEffect(Unit) { WorkplaceState.setFlag("intel_d4_gram") }   // 看內部動態＝掌握「改組」風聲(Day4 進階選項用)
    val posts = listOf(
        GramPost(R.drawable.feed_1, "加班後的小確幸，夜市犒賞自己", "42"),
        GramPost(R.drawable.feed_2, "新辦公室第一週，假裝自己很從容", "88"),
        GramPost(R.drawable.feed_3, "週末上山把腦袋清空，下週再戰", "126"),
    )

    Column(Modifier.fillMaxSize().background(Color.White).verticalScroll(scroll)) {
        // ===== 封面 =====
        Box(Modifier.fillMaxWidth().height(150.dp)) {
            Image(
                painter = painterResource(R.drawable.profile_cover),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color(0x1A000000), Color(0x66000000))),
                ),
            )
        }

        // ===== 個人資訊 =====
        Row(
            Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFF2531C)),
                contentAlignment = Alignment.Center,
            ) { Text("你", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black) }
            Spacer(Modifier.width(16.dp))
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                GramStat("3", "貼文")
                GramStat("128", "粉絲")
                GramStat("96", "追蹤")
            }
        }
        Column(Modifier.padding(start = 18.dp, end = 18.dp, top = 10.dp)) {
            Text("你 · @newbie_pm", color = Color(0xFF1F1916), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text("NovaPay 產品實習 · 第一週生還中", color = Color(0xFF6B7280), fontSize = 13.sp)
        }

        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(0.5.dp).background(Color(0x14000000)))
        Spacer(Modifier.height(4.dp))

        // ===== 動態貼文(真實照片) =====
        posts.forEach { p ->
            GramPostCard(p)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun GramStat(num: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(num, color = Color(0xFF1F1916), fontSize = 17.sp, fontWeight = FontWeight.Black)
        Text(label, color = Color(0xFF6B7280), fontSize = 12.sp)
    }
}

@Composable
private fun GramPostCard(p: GramPost) {
    var liked by remember { mutableStateOf(false) }
    val likeCount = (p.likes.toIntOrNull() ?: 0) + if (liked) 1 else 0
    Column(Modifier.fillMaxWidth().padding(top = 14.dp)) {
        Image(
            painter = painterResource(p.img),
            contentDescription = p.caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        )
        Row(
            Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                if (liked) FilledFavorite else Icons.Outlined.Favorite,
                contentDescription = "讚",
                tint = if (liked) Color(0xFFE0245E) else Color(0xFF1F1916),
                modifier = Modifier.size(24.dp).clickable { liked = !liked },
            )
            Spacer(Modifier.width(14.dp))
            Icon(Icons.Outlined.Comment, contentDescription = null, tint = Color(0xFF1F1916), modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
            Icon(Icons.Outlined.Send, contentDescription = null, tint = Color(0xFF1F1916), modifier = Modifier.size(22.dp))
        }
        Text("$likeCount 個讚", color = Color(0xFF1F1916), fontSize = 13.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 7.dp))
        Text(p.caption, color = Color(0xFF374151), fontSize = 14.sp, lineHeight = 19.sp,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 2.dp))
    }
}
