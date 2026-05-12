# GitHub Actions 自動 Build 設定步驟

每次你 push commit 到 GitHub,雲端會自動 build 一次,失敗會 email 你。

---

## Step 0 — 先在 Android Studio 本地 build 過一次

這很重要,因為要產生 `gradlew` + `gradle-wrapper.jar`(GitHub Actions 需要這兩個檔)。

1. Android Studio 打開這個專案
2. 等 Gradle Sync 完成
3. 上方選單 **Build → Make Project**(或按 Ctrl+F9)
4. 即使 build 失敗也沒關係,只要看到專案根目錄有出現 `gradlew`、`gradlew.bat`、`gradle/wrapper/gradle-wrapper.jar` 就行

確認方式:檔案總管打開專案根目錄,看到這 3 個檔就 OK。

---

## Step 1 — 註冊 GitHub 並建 Repo

1. 上 https://github.com 註冊(免費,如果你還沒有帳號)
2. 右上角 **+** → **New repository**
3. Repository name:`CareerSandbox`
4. Visibility:選 **Private**(只有你看得到)
5. **不要勾** Add README、不要勾 .gitignore、不要勾 license
6. 按 **Create repository**

建好後 GitHub 會顯示一頁有 git 指令,先不動,看 Step 2。

---

## Step 2 — 在電腦安裝 Git(如果沒裝)

1. 下載:https://git-scm.com/download/win
2. 一路下一步,預設值就好
3. 安裝後重開機,或在開始選單找「Git Bash」打開

確認安裝成功:Git Bash 輸入 `git --version`,有看到版本號就 OK。

---

## Step 3 — 把專案推上 GitHub

打開 **Git Bash**(不是 cmd!),先 cd 到專案目錄:

```bash
cd "/c/Users/Alex Lin/Downloads/CareerSandbox/CareerSandbox"
```

(注意:路徑是你實際 CareerSandbox 資料夾的位置,包含 `app/`、`build.gradle.kts` 那層)

然後依序執行:

```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
```

接下來看你 Step 1 那頁 GitHub 顯示的指令,大概長這樣:

```bash
git remote add origin https://github.com/你的帳號/CareerSandbox.git
git push -u origin main
```

第一次 push 會跳出視窗要你登入 GitHub,登入即可。

---

## Step 4 — 在 GitHub 上看 build 結果

1. 推完後,打開瀏覽器到 `https://github.com/你的帳號/CareerSandbox`
2. 上方 tab 點 **Actions**
3. 第一次會看到「Get started with GitHub Actions」,直接忽略
4. 應該會看到「Android Build」workflow 已經自動跑起來(因為 `.github/workflows/build.yml` 已經在 zip 裡)
5. 點進去看,綠色勾就是成功,紅色叉就是失敗

---

## Step 5 — Build 失敗怎麼辦

1. 點失敗那次 → 點 **build** job → 看哪一步紅色叉
2. 點開那一步,複製錯誤訊息
3. 貼給我,我修
4. 我給你新檔 → 你覆蓋到本地 → 推上去:

```bash
git add .
git commit -m "fix"
git push
```

GitHub 就會自動再 build 一次。

---

## Step 6 — Build 成功要拿 APK?

1. Actions 頁面點成功那次 build
2. 下方有「Artifacts」區塊
3. 點 **app-debug-apk** 下載 zip,裡面是 APK
4. 把 APK 拖到模擬器或實機安裝即可,不用每次都 Android Studio Run

---

## 失敗時要 email 通知我?

GitHub 預設會 email 你,但如果沒收到:

1. 右上角頭像 → **Settings**
2. 左邊 **Notifications**
3. 確認 **Actions** 那欄有勾選 Email

---

## 我這邊看什麼

你只要貼:
- 失敗的那一步的 error log(不要整份)
- 大概是哪幾行紅字

我就能定位修正。
