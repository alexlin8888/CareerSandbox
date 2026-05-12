# CareerSandbox V2

職涯探索 App MVP - V2 視覺重做版

## V2 改了什麼

* **設計系統**:從 Material 3 預設 → 深藍黑 + 橘黃漸層的商務風
  * Color.kt:新色票(`BrandOrange`、`InkBlack`、`HeroGradient` 等)
  * Type.kt:大字重數字風格,標題用 ExtraBold/Black 加負字距
  * Theme.kt:白底為主,深色 hero 卡點綴
* **動畫**
  * 頁面切換:fade + 水平滑入(NavHost 設定)
  * 按鈕按下:`Modifier.pressScale`,所有可點擊元件都帶彈性縮放
  * 卡片進場:`StaggeredAppear` 依序淡入 + 上滑
  * 首頁進度圈:0 → 64% 漸層 sweep 動畫
* **15 頁全部重做**

## 開啟方式

1. Android Studio 打開根目錄 → Gradle Sync(會自動下載依賴)
2. 連模擬器(Pixel + API 34 推薦)
3. Run

## 檔案結構

```
app/src/main/java/com/careersandbox/app/
├── MainActivity.kt
├── navigation/        Routes + NavHost
├── data/              Mock 資料
├── ui/
│   ├── theme/         Color / Type / Theme
│   ├── components/    Animations, CommonComponents, BottomNav
│   └── screens/
│       ├── onboarding/   Splash, Login, Onboarding
│       ├── home/         Home, Explore placeholder
│       ├── resume/       Hub, ExperienceList, ExperienceEdit, Editor, JdCustomize
│       ├── interview/    Hub, Setup, LiveIndividual, LiveGroup, Report
│       └── profile/      Profile
```

## 已知小問題

* `Icons.Outlined.ArrowBack` 有 deprecation 警告,不影響執行
* 若 `kotlinCompilerExtensionVersion` 與 Kotlin 版本不合,Sync 失敗時請對照
  https://developer.android.com/jetpack/androidx/releases/compose-kotlin

## 風格守則

* 繁體中文文案,UI 不用 emoji(只用 Material Icons)
* 卡片圓角:hero 28dp、一般 18-20dp、小元件 12-14dp
* 主按鈕高度 56dp、圓角 16dp
* 字級 hero 數字使用 ExtraBold / Black + 負字距

## V1 → V2 修過的 bug

* BottomNav 高度 bug(`Modifier.weight(1f).fillMaxHeight()` 撐滿整個畫面)
  * V2 改用 76dp 固定高度的 Box,內部才 `fillMaxHeight`
* `ic_launcher` mipmap 缺失導致 AAPT error
  * V2 AndroidManifest 不引用任何 icon,系統採預設
