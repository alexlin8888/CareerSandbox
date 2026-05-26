# Batch 31.9 — undraw 插畫塞回去

只動 2 個檔。`ResumeHubScreen.kt` 不用碰(沿用 31.8)。

---

## 修了什麼

### `FitAnalysisScreen.kt`
- 弧頂右側加 `R.drawable.undraw_personal_data_a1n8`
- 尺寸:175dp × 110dp(personal_data 是 1.73:1 寬比例,所以用矩形 size 避免被裁)
- 位置:`BottomEnd`,offset (-4dp, 8dp),alpha 0.95
- 標題 Column 限制 `fillMaxWidth(0.55f)` 留位置給插畫
- import 加回 `Image / painterResource / ContentScale / R`

### `CareerExplorationScreen.kt`
- 弧頂右側加 `R.drawable.undraw_exploring_fzmr`
- 尺寸:150dp × 150dp(exploring 接近正方 1.13:1,square 即可)
- 位置:`BottomEnd`,offset (-8dp, 8dp),alpha 0.95
- 標題 Column 限制 `fillMaxWidth(0.6f)`
- import 加回 `Image / painterResource / ContentScale / R`

---

## 整合(只蓋 2 個 .kt)

```bash
cd "/c/Users/Alex Lin/Downloads/CareerSandbox/CareerSandbox"

# 從 zip 抽 2 個 kt 蓋過去
unzip -o ~/Downloads/batch31_9.zip -d app/src/main/java/com/careersandbox/app/ui/screens/resume/

./gradlew assembleDebug
git add . && git commit -m "batch 31.9: add undraw personal_data + exploring to sub-page heroes" && git push
```

或手動拖 2 個 `.kt` 到 `app/src/main/java/com/careersandbox/app/ui/screens/resume/` 蓋掉舊版。

---

## 前提條件確認

你說「已新增,並轉 vector」— 請確認這兩個檔案已經放在你專案的 `app/src/main/res/drawable/`:
- `undraw_personal_data_a1n8.xml`
- `undraw_exploring_fzmr.xml`

如果檔名是別的(例如 Android Studio 自動取了 `undraw_personal_data_re_a1n8` 之類),build 會報 `Unresolved reference`,告訴我正確檔名我改一行就好。

---

## 跑出來不對時可以調的參數

**插畫太大/太小** — 改 size:
```kotlin
.size(width = 175.dp, height = 110.dp)  // Fit
.size(150.dp)                            // Career
```

**插畫被切到** — 調 offset:
```kotlin
.offset(x = (-4).dp, y = 8.dp)
```

**插畫太搶眼/太淡** — 調 alpha:
```kotlin
.alpha(0.95f)  // 1.0 = 完全不透明,0.5 = 半透
```

**標題擠到** — 調 title column 寬度:
```kotlin
.fillMaxWidth(0.55f)  // 0.5 ~ 0.65 之間
```

跑完截圖回來,我幫你微調。
