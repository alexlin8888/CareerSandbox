package com.careersandbox.app.ui.screens.sandbox2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.BrandDeepOrange
import com.careersandbox.app.ui.theme.BrandOrange
import com.careersandbox.app.ui.theme.InkSlate
import com.careersandbox.app.ui.theme.PaperWarm

@Composable
fun SandboxBriefScreen(onEnter: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWarm)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(28.dp))
        Text("背景資訊", color = InkSlate, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(18.dp))

        Body("你是 NovaPay 的資料分析師，到職第三個月。NovaPay 是一家做記帳 App 的軟體公司，台北，八十人左右。你在產品部，主管是周經理，平常最常找你的是產品經理小昀。")
        Body("今天早上小昀傳了訊息：她想推一個「收藏清單」功能，讓使用者把常看的內容存起來。下週一她要跟老闆報告做不做，需要你在今天下班前給她一份分析摘要。")
        Body("資料在「資料」視窗裡，是一張使用者行為摘要表，工程師阿凱維護。")

        Head("下班前要交的東西")
        Body("一份給小昀的摘要，三段：你發現了什麼、這份資料看不出來的事、你的建議。")

        Head("今天會發生的事")
        Body("跟平常一樣，做到一半可能有人來找你。")

        Spacer(Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BrandOrange)
                .pressScale { onEnter() },
            contentAlignment = Alignment.Center,
        ) {
            Text("進辦公室", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun Head(text: String) {
    Text(
        text, color = BrandDeepOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 6.dp),
    )
}

@Composable
private fun Body(text: String) {
    Text(
        text, color = InkSlate, fontSize = 15.sp, lineHeight = 25.sp,
        modifier = Modifier.padding(bottom = 12.dp),
    )
}