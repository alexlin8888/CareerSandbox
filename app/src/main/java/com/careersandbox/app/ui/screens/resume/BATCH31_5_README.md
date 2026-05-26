# Batch 31.5 — Integrated Version

整合進你現有 codebase 的版本。**直接覆蓋舊檔即可**,不用調 import / theme / nav。

---

## 修正了 batch 31 的 4 個問題

| 問題 | batch 31 | batch 31.5 |
|---|---|---|
| 函數簽名 | `on*` lambdas | `navController: NavHostController` ✓ |
| 顏色 token | private 定義 (#E96E3D, #D85A30) | `import com.careersandbox.app.ui.theme.*`(用 #FF6B35, #D84315) |
| 弧頂 | 自己 Canvas 畫 Q-curve | `WaveHeroBackground(gradient, heightDp)` ✓ |
| 插畫 | Canvas 手畫 ReadingFigure | `R.drawable.undraw_feedback_ebmx` + `undraw_reading_a_book_4cap` ✓ |
| Tap 回饋 | `Modifier.clickable` | `Modifier.pressScale(onClick)` ✓ |
| 資料 | hardcode | `MockData.masterResume` + `MockData.jobApplications` ✓ |

---

## 各檔案改動範圍

### `ResumeHubScreen.kt` — **外科手術**
**保留**(已經很好):
- `HeroSection()` — 完全不動
- `StatsRow()` + `SubmitRateRing()` + `MiniStatCard()` — 加 count-up 動畫
- `JobApplicationsSection()` + `JobProgressCard()` — 加 count-up + bar fill 動畫(原本 44sp 適配度數字保留,動畫從 0 算上去)

**重寫**:
- `BentoActions()` — 從「大左 + 2x2 右」改為「MASTER 全寬 + 4 透明工具」
- `BentoMaster()` — 取代 `BentoMain()`,150dp 高,左文字 + 右 `undraw_reading_a_book_4cap`
- `ToolStrip()` + `ToolButton()` — 新增,取代 `BentoSmall()`,4 個 icon 全部 `BrandDeepOrange`

### `FitAnalysisScreen.kt` — **重寫**
**新增弧頂**(原本只有 Scaffold + TopAppBar):
- `FitHeroSection()` — `WaveHeroBackground` (`BrandAmber → BrandOrange → BrandDeepOrange`) + 圓形 back button + 標題 + `undraw_feedback_ebmx`
- 內容:`FitHeroJobCard`(白卡 + 大 48sp 78%)+ Tab 列 + 6 條能力 bar + 任務卡 + 黑色 CTA

### `CareerExplorationScreen.kt` — **重寫**
- `CareerHeroSection()` — `WaveHeroBackground` (`BrandYellow → BrandAmber → BrandOrange`) + 圓形 back button + 標題 + chip + `undraw_feedback_ebmx`
- 內容:搜尋條 + chip filter + 橘色 TOP MATCH 卡(大 44sp 92%)+ 2 mini 卡 + 3 步驟學習路徑卡 + 黑色 CTA

---

## 動畫(M1 + M2 + M4 規格,DESIGN.md 第 7 章)

| 動畫 | 規格 | 套用位置 |
|---|---|---|
| **M1** 數字 count-up | 1.2s FastOutSlowIn | 3 頁所有大數字(33%, 投遞數, 適配度, FIT, 能力分數) |
| **M2** Bar fill | 1.1s + 300ms delay | 適配度 bar、能力分布 bar、Mini rec bar |
| **M4** 卡片進場 stagger | fadeIn + slideIn(it/4),550ms,delay 0/80/120ms 遞增 | 所有主要 section |

---

## 路徑檢查(對齊你的 Routes.kt)

| ToolButton | navigate 目標 |
|---|---|
| 編輯 | `Routes.RESUME_EDITOR` ✓ |
| 職涯探索 | `Routes.CAREER_EXPLORATION` ✓ |
| PDF 匯出 | `Routes.RESUME_UPLOAD_PROCESSING` ✓ |
| 適配 78% | `Routes.FIT_ANALYSIS` ✓ |

MASTER 卡點擊 → `Routes.RESUME_PROFILE` ✓
"+ 新增" 點擊 → `Routes.NEW_JOB_APPLICATION` ✓
職缺卡點擊 → `Routes.jobApplicationDetail(job.id)` ✓

---

## 已知小事

1. **「適配 78%」的 78** 目前是 hardcode 在工具條 label 上。整合時可以從 `MockData.latestFitScore` 之類的地方拿(看你資料模型)。
2. **適配分析的 hero 卡** 也是 hardcode `Junior PM / Acer / 50-80k / 10/14 截止`。整合 ViewModel 時改成 state。
3. **「開始補強」 / 「開始學習路徑」** CTA 目前 onClick `{}`。看你 reinforcement / learning path 流程要不要做才接。
4. **能力分數 + 任務** 目前在 `FitAnalysisScreen` 內 hardcode 在 `remember { listOf(...) }`。要從 `MockData` 拉的話告訴我,我加。
5. **`undraw_feedback_ebmx`** 在 3 頁都會用到(履歷 hero、適配 hero、探索 hero)。如果要差異化視覺,可以告訴我有沒有別的 undraw 資產(`undraw_target_*` / `undraw_compass_*` 之類),我換過去。

---

## 驗證指令

```bash
cd "/c/Users/Alex Lin/Downloads/CareerSandbox/CareerSandbox"

# 1. 備份(以防回退)
mkdir -p /tmp/backup
cp app/src/main/java/com/careersandbox/app/ui/screens/resume/ResumeHubScreen.kt /tmp/backup/
cp app/src/main/java/com/careersandbox/app/ui/screens/resume/FitAnalysisScreen.kt /tmp/backup/
cp app/src/main/java/com/careersandbox/app/ui/screens/resume/CareerExplorationScreen.kt /tmp/backup/

# 2. 解壓覆蓋(zip 內 3 個檔案就是 batch31.5 的路徑)
unzip -o batch31_5.zip -d app/src/main/java/com/careersandbox/app/ui/screens/resume/

# 3. 確認行數
wc -l app/src/main/java/com/careersandbox/app/ui/screens/resume/{ResumeHubScreen,FitAnalysisScreen,CareerExplorationScreen}.kt

# 4. Build
./gradlew assembleDebug

# 5. 推上去
git add app/src/main/java/com/careersandbox/app/ui/screens/resume/
git commit -m "batch 31.5: integrated v12 resume hub + v9 fit/exploration with WaveHeroBackground"
git push
```

Build 失敗就把錯誤前 5-10 行貼回來。
