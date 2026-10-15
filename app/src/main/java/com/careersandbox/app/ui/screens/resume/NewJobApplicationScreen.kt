package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.JobDto
import com.careersandbox.app.data.repository.RemoteResumeHierarchyProvider
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.launch

/**
 * 新增想比較的職缺 — two ways in, same destination (job_targets):
 *   MANUAL  type company / position / JD yourself      -> jobId = ""
 *   SEARCH  pick one from the crawled `jobs` collection -> jobId = sourceId
 * Either way the target is saved through RemoteResumeHierarchyProvider.addJobTarget
 * and the user lands on the JD customization flow.
 */
private enum class AddMode(val label: String) { MANUAL("自行輸入"), SEARCH("搜尋資料庫") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewJobApplicationScreen(navController: NavHostController) {
    var mode by rememberSaveable { mutableStateOf(AddMode.MANUAL) }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("新增想比較的職缺", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // 介紹卡
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(BrandPeach.copy(alpha = 0.45f))
                    .padding(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = BrandDeepOrange,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "從 JD 自動生第一版",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "自己輸入公司跟職位,或從資料庫挑一個現成職缺。AI 會比對你的母版生出更準的版本。",
                    color = InkGray700,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                )
            }

            Spacer(Modifier.height(20.dp))

            // 兩種方式切換
            ModeToggle(mode = mode, onChange = { mode = it })

            Spacer(Modifier.height(24.dp))

            when (mode) {
                AddMode.MANUAL -> ManualForm(navController)
                AddMode.SEARCH -> SearchPicker(navController)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ModeToggle(mode: AddMode, onChange: (AddMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(InkGray100)
            .padding(4.dp),
    ) {
        AddMode.values().forEach { m ->
            val selected = m == mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (selected) BrandOrange else androidx.compose.ui.graphics.Color.Transparent)
                    .pressScale { onChange(m) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    m.label,
                    color = if (selected) PaperWhite else InkGray500,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

/* ===== 方式一:自行輸入 ===== */
@Composable
private fun ManualForm(navController: NavHostController) {
    var company by rememberSaveable { mutableStateOf("") }
    var position by rememberSaveable { mutableStateOf("") }
    var jdText by rememberSaveable { mutableStateOf("") }

    var saving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val canSubmit = company.isNotBlank() && position.isNotBlank() && !saving

    FieldLabel("公司名稱")
    OutlinedTextField(
        value = company,
        onValueChange = { company = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("例:Acer / KKday / 字節跳動",
                color = InkGray400, style = MaterialTheme.typography.bodyMedium)
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = fieldColors(),
    )

    Spacer(Modifier.height(20.dp))

    FieldLabel("職位")
    OutlinedTextField(
        value = position,
        onValueChange = { position = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("例:Junior PM / 資料分析師 / UX 研究員",
                color = InkGray400, style = MaterialTheme.typography.bodyMedium)
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = fieldColors(),
    )

    Spacer(Modifier.height(20.dp))

    FieldLabel("職缺敘述 (JD)")
    OutlinedTextField(
        value = jdText,
        onValueChange = { jdText = it },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
        placeholder = {
            Text(
                "貼上完整職缺描述,AI 會比對你的母版找出契合段落。",
                color = InkGray400,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = fieldColors(),
    )
    if (jdText.isNotEmpty()) {
        Spacer(Modifier.height(4.dp))
        Text(
            "${jdText.length} 字元 ${if (jdText.length < 20) "(至少 20 字元)" else ""}",
            color = if (jdText.length >= 20) AccentGreen else InkGray500,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    Spacer(Modifier.height(36.dp))

    SubmitButton(
        enabled = canSubmit,
        label = if (saving) "建立中…" else "建立並 AI 生第一版",
        onClick = {
            // Save to job_targets first; only move on once the server has it.
            // A typed-in target has no jobId, and the pasted JD becomes jdSnapshot.
            saving = true
            errorMsg = null
            scope.launch {
                RemoteResumeHierarchyProvider
                    .addJobTarget(title = position, company = company, jdSnapshot = jdText)
                    .onSuccess { goToCustomize(navController, it.id) }
                    .onFailure { errorMsg = it.message ?: "建立失敗,請稍後再試" }
                saving = false
            }
        },
    )
    errorMsg?.let { ErrorLine(it) }
    if (!canSubmit && !saving) {
        Spacer(Modifier.height(8.dp))
        Text(
            "先填:" + buildList {
                if (company.isBlank()) add("公司名")
                if (position.isBlank()) add("職位")
            }.joinToString("、"),
            color = InkGray500,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/* ===== 方式二:搜尋資料庫裡的職缺 ===== */
@Composable
private fun SearchPicker(navController: NavHostController) {
    var query by rememberSaveable { mutableStateOf("") }
    var results by remember { mutableStateOf<List<JobDto>>(emptyList()) }
    var searched by remember { mutableStateOf(false) }
    var searching by remember { mutableStateOf(false) }
    var page by remember { mutableIntStateOf(1) }
    var total by remember { mutableIntStateOf(0) }
    var loadingMore by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf<String?>(null) }

    // sourceId -> created target id. Survives new searches so an already-added job
    // shows as added again if it reappears in later results.
    val added = remember { mutableStateMapOf<String, String>() }
    var busyId by remember { mutableStateOf<String?>(null) }
    var detailJob by remember { mutableStateOf<JobDto?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Show a random sample before the first search so the list is never empty.
    LaunchedEffect(Unit) {
        if (searched) return@LaunchedEffect
        searching = true
        try {
            val res = ApiClient.jobApi.random(10)
            if (res.isSuccessful) results = res.body()?.data ?: emptyList()
        } catch (_: Exception) { }
        searching = false
    }

    fun runSearch() {
        if (query.isBlank() || searching) return
        searching = true
        errorMsg = null
        selectedId = null
        scope.launch {
            try {
                val res = ApiClient.jobApi.search(query.trim(), page = 1)
                if (res.isSuccessful) {
                    val body = res.body()
                    results = body?.data ?: emptyList()
                    total = body?.pagination?.total ?: results.size
                    page = 1
                } else errorMsg = "搜尋失敗（錯誤碼 ${res.code()}）"
            } catch (e: Exception) {
                errorMsg = "無法連線到伺服器"
            }
            searched = true
            searching = false
        }
    }

    fun loadMore() {
        if (loadingMore || results.size >= total) return
        loadingMore = true
        scope.launch {
            try {
                val res = ApiClient.jobApi.search(query.trim(), page = page + 1)
                if (res.isSuccessful) {
                    results = results + (res.body()?.data ?: emptyList())
                    page += 1
                } else errorMsg = "載入失敗（錯誤碼 ${res.code()}）"
            } catch (e: Exception) {
                errorMsg = "無法連線到伺服器"
            }
            loadingMore = false
        }
    }

    fun add(job: JobDto) {
        val sid = job.sourceId ?: return
        busyId = sid
        errorMsg = null
        scope.launch {
            val jd = listOfNotNull(job.description, job.conditionOther)
                .filter { it.isNotBlank() }
                .joinToString("\n")
            RemoteResumeHierarchyProvider
                .addJobTarget(
                    title = job.title.orEmpty(),
                    company = job.company.orEmpty(),
                    jdSnapshot = jd,
                    jobId = sid,
                    jdKeywords = job.requiredSkills ?: emptyList(),
                )
                .onSuccess { added[sid] = it.id; selectedId = null }
                .onFailure { errorMsg = it.message ?: "新增失敗,請稍後再試" }
            busyId = null
        }
    }

    fun remove(job: JobDto) {
        val sid = job.sourceId ?: return
        val targetId = added[sid] ?: return
        busyId = sid
        errorMsg = null
        scope.launch {
            RemoteResumeHierarchyProvider.removeJobTarget(targetId)
                .onSuccess { added.remove(sid) }
                .onFailure { errorMsg = it.message ?: "移除失敗,請稍後再試" }
            busyId = null
        }
    }

    FieldLabel("關鍵字")
    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("例:資料分析 / 行銷企劃 / 中國信託",
                color = InkGray400, style = MaterialTheme.typography.bodyMedium)
        },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = InkGray400) },
        trailingIcon = {
            TextButton(onClick = { runSearch() }, enabled = query.isNotBlank() && !searching) {
                Text(if (searching) "搜尋中…" else "搜尋", color = BrandDeepOrange, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = { runSearch() }),
        colors = fieldColors(),
    )

    Spacer(Modifier.height(16.dp))

    when {
        searching -> Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandDeepOrange)
        }
        searched && results.isEmpty() -> Text(
            "找不到符合的職缺,換個關鍵字試試。",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        )
        results.isNotEmpty() -> {
            Text(
                if (searched) "顯示 ${results.size} / $total 筆,點擊新增或查看詳細資料。"
                else "隨機推薦 ${results.size} 筆,或輸入關鍵字搜尋",
                color = InkGray500,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(10.dp))
            results.forEach { job ->
                val sid = job.sourceId
                JobResultCard(
                    job = job,
                    selected = sid != null && selectedId == sid,
                    isAdded = sid != null && added.containsKey(sid),
                    busy = sid != null && busyId == sid,
                    onClick = { if (sid != null && !added.containsKey(sid)) selectedId = if (selectedId == sid) null else sid },
                    onAdd = { add(job) },
                    onRemove = { remove(job) },
                    onDetail = { detailJob = job },
                )
                Spacer(Modifier.height(10.dp))
            }
            if (searched && results.size < total) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, InkGray200, RoundedCornerShape(12.dp))
                        .pressScale(enabled = !loadingMore, onClick = { loadMore() }),
                    contentAlignment = Alignment.Center,
                ) {
                    if (loadingMore) CircularProgressIndicator(color = BrandDeepOrange, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("載入更多（還有 ${total - results.size} 筆）", color = InkGray700, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }

    errorMsg?.let { ErrorLine(it) }

    if (added.isNotEmpty()) {
        Spacer(Modifier.height(24.dp))
        SubmitButton(
            enabled = busyId == null,
            label = "完成,去客製化（已加 ${added.size} 個）",
            onClick = { goToCustomize(navController, added.values.first()) },
        )
    }
    detailJob?.let { JobDetailSheet(job = it, onDismiss = { detailJob = null }) }
}

@Composable
private fun JobResultCard(
    job: JobDto,
    selected: Boolean,
    isAdded: Boolean,
    busy: Boolean,
    onClick: () -> Unit,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onDetail: () -> Unit,
) {
    val bg = when {
        isAdded -> AccentGreen.copy(alpha = 0.10f)
        selected -> BrandPeach.copy(alpha = 0.45f)
        else -> InkGray100
    }
    val borderColor = when {
        isAdded -> AccentGreen
        selected -> BrandOrange
        else -> androidx.compose.ui.graphics.Color.Transparent
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(if (isAdded || selected) 1.5.dp else 0.dp, borderColor, RoundedCornerShape(14.dp))
            .pressScale(enabled = !isAdded && !busy, onClick = onClick)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(job.title.orEmpty(), color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text(
                    listOfNotNull(job.company, job.location?.takeIf { it.isNotBlank() }).joinToString(" · "),
                    color = InkGray500, fontSize = 12.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                job.salaryRaw?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(it, color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            if (busy) {
                Spacer(Modifier.width(10.dp))
                CircularProgressIndicator(color = BrandDeepOrange, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else if (isAdded) {
                Spacer(Modifier.width(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("已新增", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Action row: appears once the card is selected (or added, so it can still be opened / removed).
        if ((selected || isAdded) && !busy) {
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallAction(label = "詳細", filled = false, onClick = onDetail)
                if (isAdded) SmallAction(label = "移除", filled = false, onClick = onRemove)
                else SmallAction(label = "新增", filled = true, onClick = onAdd)
            }
        }
    }
}

@Composable
private fun SmallAction(label: String, filled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (filled) BrandOrange else PaperWhite)
            .border(1.dp, if (filled) BrandOrange else InkGray200, RoundedCornerShape(10.dp))
            .pressScale(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
    ) {
        Text(label, color = if (filled) PaperWhite else InkGray700, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

/* ===== 職缺詳細 ===== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JobDetailSheet(job: JobDto, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PaperWhite,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(job.company.orEmpty(), color = InkGray500, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(job.title.orEmpty(), color = InkBlack, fontWeight = FontWeight.Black, fontSize = 20.sp, lineHeight = 26.sp)

            // Quick facts
            val facts = listOfNotNull(
                job.salaryRaw?.takeIf { it.isNotBlank() }?.let { "薪資" to it },
                job.location?.takeIf { it.isNotBlank() }?.let { "地點" to it },
                job.industry?.takeIf { it.isNotBlank() }?.let { "產業" to it },
                job.education?.takeIf { it.isNotBlank() }?.let { "學歷" to it },
                job.workExp?.takeIf { it.isNotBlank() }?.let { "經驗" to it },
            )
            if (facts.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(InkGray100)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    facts.forEach { (k, v) ->
                        Row {
                            Text(k, color = InkGray500, fontSize = 13.sp, modifier = Modifier.width(44.dp))
                            Text(v, color = InkBlack, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            DetailSection("職缺敘述", job.description)
            DetailChips("所需技能", job.requiredSkills)
            DetailSection("其他條件", job.conditionOther)
            DetailChips("職務類別", job.jobCategory)
            DetailChips("福利", job.tags)
        }
    }
}

@Composable
private fun DetailSection(title: String, body: String?) {
    val text = body?.trim()?.takeIf { it.isNotBlank() } ?: return
    Spacer(Modifier.height(20.dp))
    Text(title, color = InkGray500, style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Black, letterSpacing = 3.sp)
    Spacer(Modifier.height(8.dp))
    Text(text, color = InkBlack, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun DetailChips(title: String, items: List<String>?) {
    val list = items?.filter { it.isNotBlank() }?.takeIf { it.isNotEmpty() } ?: return
    Spacer(Modifier.height(20.dp))
    Text(title, color = InkGray500, style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Black, letterSpacing = 3.sp)
    Spacer(Modifier.height(8.dp))
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        list.forEach { item ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(BrandPeach.copy(alpha = 0.4f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(item, color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/* ===== shared ===== */

// Land on the customization flow. The new target is at the top of the list there.
// TODO: if Routes gains a jdCustomize(targetId) helper, pass targetId to preselect it.
private fun goToCustomize(navController: NavHostController, @Suppress("UNUSED_PARAMETER") targetId: String) {
    navController.navigate(Routes.JD_CUSTOMIZE) {
        popUpTo(Routes.RESUME_HUB) { inclusive = false }
    }
}

@Composable
private fun SubmitButton(enabled: Boolean, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) InkBlack else InkGray300)
            .pressScale(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = if (enabled) BrandAmber else InkGray500,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                label,
                color = if (enabled) PaperWhite else InkGray500,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ErrorLine(msg: String) {
    Spacer(Modifier.height(8.dp))
    Text(
        msg,
        color = AccentRed,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BrandOrange,
    unfocusedBorderColor = InkGray200,
)

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        color = InkGray500,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Black,
        letterSpacing = 3.sp,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}