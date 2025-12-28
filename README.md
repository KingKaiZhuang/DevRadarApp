# DevRadar App 專案說明文件

## AI 使用範圍聲明

本專案在開發過程中使用了人工智慧 (AI) 工具協助，具體使用範圍如下：

1. **程式碼輔助**：協助生成基礎樣板代碼 (Boilerplate Code)、Jetpack Compose UI 元件、以及 Room 資料庫設定。
2. **問題排查**：協助分析並解決編譯錯誤 (Compilation Errors) 與執行時期錯誤 (Runtime Exceptions)。
3. **功能實作**：提供特定功能（如 RecyclerView 到 LazyColumn 的遷移、導航圖設定）的實作建議與範例。
4. **文檔撰寫**：協助撰寫與格式化專案 README 文件。

> 聲明：雖然使用了 AI 協助，但在整合至專案前，所有程式碼與設計皆經過人工審閱、修改與測試，以確保其正確性與符合專案需求。

## 專案簡介

DevRadar 是一款使用 Jetpack Compose 建構的現代化 Android 應用程式，專門彙整並顯示來自 iThome 的開發者新聞。它具備簡潔的深色主題介面、離線收藏功能以及智慧文章分類。

## 功能特色

### 文章探索
* **每日熱門新聞**：從 DevRadar API 獲取最新的熱門文章。
* **動態分類**：文章會自動標記分類（例如：Frontend、Backend、AI/Data、Mobile）。
* **互動式篩選**：使用動態標籤按類別篩選文章。沒有文章的分類會自動隱藏。
* **日期排序**：可切換「最新優先」或「最舊優先」以尋找感興趣的內容。

### 智慧收藏
* **離線存取**：收藏的文章使用 Room Database 本地儲存，隨時可讀。
* **分類保留**：即使在離線狀態下，收藏的文章也會保留其分類標籤。
* **輕鬆管理**：直接從探索頁面或專用的收藏頁面新增或移除最愛文章。

### 現代化 UI/UX
* **Jetpack Compose**：完全使用 Compose 構建，確保流暢、反應靈敏且現代的使用者介面。
* **深色模式**：專為開發者設計的深色石板色調（#0F172A），長時間閱讀也不易疲勞。
* **Material 3**：採用最新的 Material Design 設計元件。

## 技術堆疊

* **語言**: Kotlin
* **UI 工具包**: Jetpack Compose
* **架構**: MVVM (Model-View-ViewModel)
* **網路連線**: Retrofit + Gson
* **本地儲存**: Room Database (SQLite)
* **非同步處理**: Kotlin Coroutines & Flow

## 專案結構

```text
com.example.devradarapp
├── data/
│   ├── AppDatabase.kt       # Room 資料庫設定
│   ├── ArticleDetails.kt    # API 資料模型
│   ├── ArticleRepository.kt # 資料存取與統一管理
│   ├── FavoriteDao.kt       # 本地儲存資料存取物件 (DAO)
│   └── RetrofitInstance.kt  # 網路客戶端實例
├── model/
│   ├── Article.kt           # 領域模型 (包含分類欄位)
│   ├── FavoriteEntity.kt    # 資料庫實體
│   └── UserEntity.kt        # 使用者實體
├── ui/
│   ├── ExploreScreen.kt     # 主頁面 (包含篩選、排序、列表)
│   ├── FavoritesScreen.kt   # 本地收藏頁面
│   ├── LoginScreen.kt       # 使用者登入
│   ├── MainScreen.kt        # 導航主機 (Host)
│   ├── OnboardingScreen.kt  # 歡迎頁面
│   ├── ProfileScreen.kt     # 個人檔案
│   └── Theme.kt             # 設計系統 (顏色、字體)
├── viewmodel/
│   └── ArticleViewModel.kt  # 文章與收藏的狀態管理
└── utils/
    └── BrowserUtils.kt      # Chrome Custom Tabs 輔助工具
