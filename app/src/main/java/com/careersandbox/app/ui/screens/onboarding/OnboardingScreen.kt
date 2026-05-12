package com.careersandbox.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    var step by remember { mutableIntStateOf(1) }
    val total = 4
    val interests = remember { mutableStateListOf<String>() }
    val skillsHave = remember { mutableStateListOf<String>() }
    val skillsWant = remember { mutableStateListOf<String>() }
    var name by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(PaperOff).padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(56.dp))
        // 進度
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(total) { i ->
                Box(
                    Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(50))
                        .background(if (i + 1 <= step) BrandOrange else InkGray200)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Step $step / $total", style = MaterialTheme.typography.labelSmall, color = InkGray400)

        Spacer(Modifier.height(28.dp))

        AnimatedContent(
            targetState = step,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            transitionSpec = {
                (fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 })
                    .togetherWith(fadeOut(tween(150)))
            },
            label = "step",
        ) { current ->
            Column(Modifier.verticalScroll(rememberScrollState())) {
                when (current) {
                    1 -> Step1(name, { name = it }, school, { school = it },
                        dept, { dept = it }, year, { year = it })
                    2 -> Step2(interests)
                    3 -> Step3(skillsHave, skillsWant)
                    4 -> Step4()
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
            if (step > 1) {
                SecondaryButton(text = "上一步", onClick = { step-- },
                    modifier = Modifier.weight(1f))
                Spacer(Modifier.width(12.dp))
            }
            PrimaryDarkButton(
                text = if (step == total) "開始使用" else "下一步",
                onClick = { if (step < total) step++ else onDone() },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun Step1(
    name: String, onName: (String) -> Unit,
    school: String, onSchool: (String) -> Unit,
    dept: String, onDept: (String) -> Unit,
    year: String, onYear: (String) -> Unit,
) {
    Text("先認識你", style = MaterialTheme.typography.headlineLarge,
        color = InkBlack, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(8.dp))
    Text("這些只用來推薦合適內容", color = InkGray500, style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(32.dp))
    OnboardField("姓名", name, onName)
    OnboardField("學校", school, onSchool)
    OnboardField("系所", dept, onDept)
    OnboardField("年級", year, onYear)
}

@Composable
private fun OnboardField(label: String, value: String, onChange: (String) -> Unit) {
    Column(Modifier.padding(bottom = 14.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge,
            color = InkGray700, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = InkBlack,
                unfocusedBorderColor = InkGray200,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            )
        )
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun Step2(selected: MutableList<String>) {
    Text("你想探索的方向", style = MaterialTheme.typography.headlineLarge,
        color = InkBlack, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(8.dp))
    Text("選 3-5 個,可以隨時改 ・ 已選 ${selected.size}/5",
        color = InkGray500, style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(24.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MockData.jobInterests.forEach { item ->
            PillChip(label = item, selected = item in selected, onClick = {
                if (item in selected) selected.remove(item)
                else if (selected.size < 5) selected.add(item)
            })
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun Step3(skillsHave: MutableList<String>, skillsWant: MutableList<String>) {
    Text("你會什麼", style = MaterialTheme.typography.headlineLarge,
        color = InkBlack, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(8.dp))
    Text("先盤點手上有的", color = InkGray500, style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(24.dp))
    Text("我擅長", style = MaterialTheme.typography.titleMedium,
        color = InkBlack, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(10.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MockData.skills.forEach { s ->
            PillChip(s, selected = s in skillsHave) {
                if (s in skillsHave) skillsHave.remove(s) else skillsHave.add(s)
            }
        }
    }
    Spacer(Modifier.height(22.dp))
    Text("我想學", style = MaterialTheme.typography.titleMedium,
        color = InkBlack, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(10.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MockData.skills.forEach { s ->
            PillChip(s, selected = s in skillsWant) {
                if (s in skillsWant) skillsWant.remove(s) else skillsWant.add(s)
            }
        }
    }
}

@Composable
private fun Step4() {
    Text("準備好了", style = MaterialTheme.typography.headlineLarge,
        color = InkBlack, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(8.dp))
    Text("根據你的選擇,以下幾件事可以先做",
        color = InkGray500, style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(24.dp))

    val items = listOf(
        "01" to ("先試試模擬一場面試" to "用團體面試體驗看看會怎樣"),
        "02" to ("12 個適合你的職位" to "可以從職場沙盒裡看真實樣貌"),
        "03" to ("先寫一份履歷草稿" to "從你過去的經驗開始"),
    )
    items.forEachIndexed { idx, (no, content) ->
        StaggeredAppear(delayMillis = idx * 100) {
            WhiteCard(modifier = Modifier.padding(bottom = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(no, style = MaterialTheme.typography.displaySmall,
                        color = BrandOrange, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(content.first, style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold, color = InkBlack)
                        Spacer(Modifier.height(2.dp))
                        Text(content.second, style = MaterialTheme.typography.bodySmall,
                            color = InkGray500)
                    }
                }
            }
        }
    }
}
