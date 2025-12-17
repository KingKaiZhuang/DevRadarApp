# DevRadar App 📱

**DevRadar** 是一款使用 **Jetpack Compose** 建構的現代化 Android 應用程式，專門彙整並顯示來自 iThome 的開發者新聞。它具備簡潔的深色主題介面、離線收藏功能以及智慧文章分類。

## ✨ 功能特色

### 📰 文章探索
- **每日熱門新聞**：從 DevRadar API 獲取最新的熱門文章。
- **動態分類**：文章會自動標記分類（例如：`Frontend`、`Backend`、`AI/Data`、`Mobile`）。
- **互動式篩選**：使用動態標籤按類別篩選文章。沒有文章的分類會自動隱藏。
- **日期排序**：可切換「最新優先」或「最舊優先」以尋找您感興趣的內容。

### ❤️ 智慧收藏
- **離線存取**：收藏的文章使用 **Room Database** 本地儲存，隨時可讀。
- **分類保留**：即使在離線狀態下，收藏的文章也會保留其分類標籤。
- **輕鬆管理**：直接從探索頁面或專用的收藏頁面新增或移除最愛文章。

### 🎨 現代化 UI/UX
- **Jetpack Compose**：完全使用 Compose 構建，確保流暢、反應靈敏且現代的使用者介面。
- **深色模式**：專為開發者設計的深色石板色調（`#0F172A`），長時間閱讀也不累。
- **Material 3**：採用最新的 Material Design 設計元件。

## 🛠️ 技術堆疊

- **語言**: [Kotlin](https://kotlinlang.org/)
- **UI 工具包**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **架構**: MVVM (Model-View-ViewModel)
- **網路連線**: [Retrofit](https://square.github.io/retrofit/) + Gson
- **本地儲存**: [Room Database](https://developer.android.com/training/data-storage/room) (SQLite)
- **非同步處理**: Kotlin Coroutines & Flow

## 📂 專案結構

```
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
```

## 🚀 快速開始

1.  **後端設定**：確保 Python Scraper API 正在運行。（請參閱後端 `README.md`）。
    *   *注意：如果在實體設備上運行，請使用您的本地 IP 位址更新 `RetrofitInstance.kt`。*
2.  **在 Android Studio 中開啟**：從根目錄匯入專案。
3.  **同步 Gradle**：允許 Android Studio 下載所有依賴項。
4.  **運行**：選擇模擬器或已連接的設備，然後點擊 **Run**。

## 🔄 近期更新

*   **v1.1**: 新增伺服器端文章分類功能。
*   **v1.2**: 實作客戶端分類篩選與日期排序功能。
*   **v1.3**: 遷移至僅客戶端收藏模式 (Room DB v4)。

---
*Built for developers, by developers.*
