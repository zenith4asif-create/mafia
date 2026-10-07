package com.example

import java.util.UUID

enum class ThemeMode {
  SYSTEM, LIGHT, DARK
}

enum class ActiveSheet {
  NONE,
  SETTINGS,
  TABS,
  BOOKMARKS,
  HISTORY,
  DOWNLOADS,
  CLEAR_DATA,
  ABOUT,
  HELP
}

data class BrowserTab(
  val id: String = UUID.randomUUID().toString(),
  val title: String = "New Tab",
  val url: String = "",
  val isIncognito: Boolean = false,
  val canGoBack: Boolean = false,
  val canGoForward: Boolean = false,
  val isLoading: Boolean = false,
  val progress: Int = 0
)

data class HistoryItem(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val url: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class BookmarkItem(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val url: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class DownloadItem(
  val id: String = UUID.randomUUID().toString(),
  val fileName: String,
  val url: String,
  val mimeType: String = "",
  val contentLength: Long = 0L,
  val timestamp: Long = System.currentTimeMillis()
)

data class BrowserSettings(
  val themeMode: ThemeMode = ThemeMode.SYSTEM,
  val showProgressBar: Boolean = true,
  val adBlockerEnabled: Boolean = true,
  val excessiveAdsBlockEnabled: Boolean = true,
  val blockThirdPartyCookies: Boolean = true,
  val desktopMode: Boolean = false
)
