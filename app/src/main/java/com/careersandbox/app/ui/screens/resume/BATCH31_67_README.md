# Batch 31.6 + 31.7 合併包

**3 個檔全部覆蓋過去就好**,跳過 31.6 直接到 31.7。

---

## 為什麼一起包

你還沒套 31.6,所以 31.6 修的 fit/exploration 弧頂插畫 + 搜尋條 + chip 細節,跟 31.7 修的履歷頁投遞統計 + 職缺卡,**全部一次給你**。

---

## 3 個檔對應的修正

### `ResumeHubScreen.kt`(來自 31.7)
- **投遞統計卡** 整塊 wrap 白底 20dp 圓角
- Label 改「本月已投遞」+「回覆率」
- 加 `●已回覆 N` + `●審核中 M` 兩個狀態點
- **職缺卡** 白底 + 42dp avatar 框(第 0 張橘黃漸層、其他 peach)
- 38sp `BrandDeepOrange` % + 3 個 chips「N 版本 / N 投遞 / 狀態」+ 4dp 純色 bar
- Bento + 「針對職缺」padding 統一 14.dp
- Mini cards 改純白底

### `FitAnalysisScreen.kt`(來自 31.6)
- **弧頂插畫** 從 `undraw_feedback_ebmx` 換成 Canvas 畫 **target(3 同心圓 + 箭頭) + 5 條 bar chart**(中間黃)
- 弧頂內加 chip:白底 + 圓 A 頭像 + Junior PM · Acer

### `CareerExplorationScreen.kt`(來自 31.6)
- **弧頂插畫** 從 `undraw_feedback_ebmx` 換成 Canvas 畫 **compass(圓 + 十字 + N 箭頭) + 虛線 Q-curve + 3 點**
- **搜尋條** bg 改 `PaperWhite` + 1.dp `InkGray100` border
- **Filter chips** inactive 改 `PaperWhite` + 1.dp `InkGray200` border(原本灰底看不見)

---

## 整合(3 個檔覆蓋)

```bash
cd "/c/Users/Alex Lin/Downloads/CareerSandbox/CareerSandbox"

# 一次蓋 3 個檔
unzip -o ~/Downloads/batch31_67.zip -d app/src/main/java/com/careersandbox/app/ui/screens/resume/

./gradlew assembleDebug
git add . && git commit -m "batch 31.6+31.7: v12 stats card, job cards w/ avatar+chips, v9 arc illustrations, search/filter borders" && git push
```

或檔案總管手動拖 3 個 `.kt` 蓋過去。

---

## Build 預期會新增的 import

`CareerExplorationScreen.kt` 新加:
```kotlin
import androidx.compose.foundation.border
```

這個本來就在 `androidx.compose.foundation` 套件裡,你的 dependencies 應該已經有,不用改 build.gradle。
