package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.JobTarget
import com.careersandbox.app.data.mock.MockResumeHierarchyProvider
import com.careersandbox.app.data.mock.ResumeMaster
import com.careersandbox.app.data.mock.ResumeVersion
import com.careersandbox.app.data.mock.SubmissionStatus
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeHierarchyScreen(navController: NavHostController) {
    val master = MockResumeHierarchyProvider.master()
    val targets = MockResumeHierarchyProvider.jobTargets()
    var showAddDialog by remember { mutableStateOf(false) }
    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = { Text("職缺與版本", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                actions = {
                    TextButton(onClick = { navController.navigate(Routes.RESUME_ARCH_INTRO) }) {
                        Text("怎麼用", color = BrandDeepOrange, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp,
                top = pad.calculateTopPadding() + 4.dp, bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { MasterCard(master) }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("職缺 (${targets.size})", color = InkBlack,
                            fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Spacer(Modifier.height(2.dp))
                        Text("每個應徵目標一張,點開看各版本與投遞狀態",
                            color = InkGray500, fontSize = 12.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(BrandPeach)
                            .pressScale { showAddDialog = true }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null,
                            tint = BrandDeepOrange, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("新增", color = BrandDeepOrange,
                            fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            items(targets, key = { it.id }) { target -> JobTargetCard(target) }
            if (targets.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(PaperWhite)
                            .padding(20.dp),
                    ) {
                        Text("還沒有職缺", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(Modifier.height(3.dp))
                        Text("按右上「新增」建立第一個應徵目標", color = InkGray500, fontSize = 12.sp)
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
        if (showAddDialog) {
            AddTargetDialog(
                onAdd = { t, c ->
                    MockResumeHierarchyProvider.addJobTarget(t, c)
                    showAddDialog = false
                },
                onDismiss = { showAddDialog = false },
            )
        }
    }
}

@Composable
private fun MasterCard(master: ResumeMaster) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BrandDeepOrange)
            .padding(18.dp),
    ) {
        Text("母版 ・ 總表", color = PaperWhite.copy(alpha = 0.85f),
            fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
        Spacer(Modifier.height(6.dp))
        Text("${master.ownerName} 的完整履歷", color = PaperWhite,
            fontWeight = FontWeight.Black, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Text("所有客製版本都從這份取材,它本身不投出去",
            color = PaperWhite.copy(alpha = 0.9f), fontSize = 12.sp, lineHeight = 16.sp)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoPill("${master.experienceCount} 段經歷")
            InfoPill("${master.skills.size} 項技能")
        }
    }
}

@Composable
private fun InfoPill(text: String) {
    Box(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PaperWhite.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(text, color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun JobTargetCard(target: JobTarget) {
    var expanded by remember { mutableStateOf(false) }
    var editingVersion by remember { mutableStateOf<ResumeVersion?>(null) }
    var versionToDelete by remember { mutableStateOf<ResumeVersion?>(null) }
    var showDeleteTarget by remember { mutableStateOf(false) }
    WhiteCard(onClick = { expanded = !expanded }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("${target.title} ・ ${target.company}", color = InkBlack,
                    fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(3.dp))
                Text("${target.versions.size} 個版本", color = InkGray500, fontSize = 12.sp)
            }
            target.versions.firstOrNull()?.status?.let { StatusBadge(it) }
            Spacer(Modifier.width(8.dp))
            Icon(
                if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null, tint = InkGray400,
            )
        }
        androidx.compose.animation.AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(12.dp))
                if (target.jdKeywords.isNotEmpty()) {
                    Text("這個 JD 重視", color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        target.jdKeywords.forEach { kw ->
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(BrandPeach)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Text(kw, color = BrandDeepOrange, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Box(Modifier.fillMaxWidth().height(1.dp).background(InkGray100))
                    Spacer(Modifier.height(12.dp))
                }
                if (target.versions.isEmpty()) {
                    Text("還沒有版本", color = InkBlack, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(Modifier.height(2.dp))
                    Text("到 JD 客製化,把客製存成這個職缺的版本",
                        color = InkGray500, fontSize = 12.sp, lineHeight = 16.sp)
                } else {
                    target.versions.forEachIndexed { idx, v ->
                        VersionRow(
                            v = v,
                            onEditStatus = { editingVersion = it },
                            onDuplicate = { MockResumeHierarchyProvider.duplicateVersion(v.id) },
                            onDelete = { versionToDelete = v },
                        )
                        if (idx < target.versions.size - 1) Spacer(Modifier.height(12.dp))
                    }
                }
                Spacer(Modifier.height(14.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(InkGray100))
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.pressScale { showDeleteTarget = true },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null,
                        tint = AccentRed, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("刪除此職缺", color = AccentRed,
                        fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
    editingVersion?.let { ver ->
        StatusPickerDialog(
            current = ver.status,
            onPick = { newStatus ->
                MockResumeHierarchyProvider.updateVersionStatus(ver.id, newStatus)
                editingVersion = null
            },
            onDismiss = { editingVersion = null },
        )
    }
    versionToDelete?.let { ver ->
        AlertDialog(
            onDismissRequest = { versionToDelete = null },
            confirmButton = {
                TextButton(onClick = {
                    MockResumeHierarchyProvider.removeVersion(ver.id)
                    versionToDelete = null
                }) { Text("刪除") }
            },
            dismissButton = {
                TextButton(onClick = { versionToDelete = null }) { Text("取消") }
            },
            title = { Text("刪除版本") },
            text = { Text("確定刪除「${ver.label}」?此動作無法復原。") },
        )
    }
    if (showDeleteTarget) {
        AlertDialog(
            onDismissRequest = { showDeleteTarget = false },
            confirmButton = {
                TextButton(onClick = {
                    MockResumeHierarchyProvider.removeJobTarget(target.id)
                    showDeleteTarget = false
                }) { Text("刪除") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteTarget = false }) { Text("取消") }
            },
            title = { Text("刪除職缺") },
            text = { Text("確定刪除「${target.title}・${target.company}」?其下 ${target.versions.size} 個版本會一起刪除,無法復原。") },
        )
    }
}

@Composable
private fun VersionRow(
    v: ResumeVersion,
    onEditStatus: (ResumeVersion) -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(v.label, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.width(8.dp))
            Row(
                modifier = Modifier.pressScale { onEditStatus(v) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusBadge(v.status)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Outlined.Edit, contentDescription = "變更狀態",
                    tint = InkGray400, modifier = Modifier.size(13.dp))
            }
            Spacer(Modifier.weight(1f))
            Box {
                IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = "更多",
                        tint = InkGray400, modifier = Modifier.size(18.dp))
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("複製為新版本") },
                        onClick = { menuOpen = false; onDuplicate() },
                    )
                    DropdownMenuItem(
                        text = { Text("刪除版本") },
                        onClick = { menuOpen = false; onDelete() },
                    )
                }
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(v.note, color = InkGray500, fontSize = 12.sp, lineHeight = 16.sp)
        v.submittedDate?.let {
            Spacer(Modifier.height(2.dp))
            Text("投遞於 $it", color = InkGray400, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AddTargetDialog(onAdd: (String, String) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onAdd(title.trim(), company.trim()) },
                enabled = title.isNotBlank() && company.isNotBlank(),
            ) { Text("新增") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
        title = { Text("新增職缺") },
        text = {
            Column {
                Text("先建職缺,之後在 JD 客製化把客製存成它的版本",
                    color = InkGray500, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("職位") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("公司") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    )
}

@Composable
private fun StatusPickerDialog(
    current: SubmissionStatus,
    onPick: (SubmissionStatus) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
        title = { Text("更新投遞狀態") },
        text = {
            Column {
                SubmissionStatus.values().forEach { s ->
                    val selected = s == current
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) BrandPeach else InkGray100)
                            .pressScale { onPick(s) }
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        StatusBadge(s)
                        Spacer(Modifier.weight(1f))
                        if (selected) {
                            Icon(Icons.Outlined.Check, contentDescription = null,
                                tint = BrandDeepOrange, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        },
    )
}

@Composable
private fun StatusBadge(status: SubmissionStatus) {
    val color: Color = when (status) {
        SubmissionStatus.DRAFT -> InkGray400
        SubmissionStatus.SUBMITTED -> AccentBlue
        SubmissionStatus.INTERVIEWING -> BrandOrange
        SubmissionStatus.WAITING -> BrandAmber
        SubmissionStatus.REJECTED -> AccentRed
        SubmissionStatus.OFFER -> AccentGreen
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(status.label, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}
