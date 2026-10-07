package com.example

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class BrowserRepository(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("o2_browser_lite_repository", Context.MODE_PRIVATE)

  // --- Settings Persistence ---

  fun loadSettings(): BrowserSettings {
    val themeName = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
    val themeMode = try { ThemeMode.valueOf(themeName) } catch (_: Exception) { ThemeMode.SYSTEM }

    return BrowserSettings(
      themeMode = themeMode,
      showProgressBar = prefs.getBoolean(KEY_SHOW_PROGRESS_BAR, true),
      adBlockerEnabled = prefs.getBoolean(KEY_AD_BLOCKER, true),
      excessiveAdsBlockEnabled = prefs.getBoolean(KEY_EXCESSIVE_ADS, true),
      blockThirdPartyCookies = prefs.getBoolean(KEY_BLOCK_THIRD_PARTY_COOKIES, true),
      desktopMode = prefs.getBoolean(KEY_DESKTOP_MODE, false)
    )
  }

  fun saveSettings(settings: BrowserSettings) {
    prefs.edit()
      .putString(KEY_THEME_MODE, settings.themeMode.name)
      .putBoolean(KEY_SHOW_PROGRESS_BAR, settings.showProgressBar)
      .putBoolean(KEY_AD_BLOCKER, settings.adBlockerEnabled)
      .putBoolean(KEY_EXCESSIVE_ADS, settings.excessiveAdsBlockEnabled)
      .putBoolean(KEY_BLOCK_THIRD_PARTY_COOKIES, settings.blockThirdPartyCookies)
      .putBoolean(KEY_DESKTOP_MODE, settings.desktopMode)
      .apply()
  }

  // --- Bookmarks Persistence ---

  fun loadBookmarks(): List<BookmarkItem> {
    val jsonString = prefs.getString(KEY_BOOKMARKS, "[]") ?: "[]"
    val list = mutableListOf<BookmarkItem>()
    try {
      val jsonArray = JSONArray(jsonString)
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          BookmarkItem(
            id = obj.getString("id"),
            title = obj.getString("title"),
            url = obj.getString("url"),
            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
          )
        )
      }
    } catch (_: Exception) {}
    return list
  }

  fun saveBookmarks(bookmarks: List<BookmarkItem>) {
    val jsonArray = JSONArray()
    for (item in bookmarks) {
      val obj = JSONObject().apply {
        put("id", item.id)
        put("title", item.title)
        put("url", item.url)
        put("timestamp", item.timestamp)
      }
      jsonArray.put(obj)
    }
    prefs.edit().putString(KEY_BOOKMARKS, jsonArray.toString()).apply()
  }

  // --- History Persistence ---

  fun loadHistory(): List<HistoryItem> {
    val jsonString = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
    val list = mutableListOf<HistoryItem>()
    try {
      val jsonArray = JSONArray(jsonString)
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          HistoryItem(
            id = obj.getString("id"),
            title = obj.getString("title"),
            url = obj.getString("url"),
            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
          )
        )
      }
    } catch (_: Exception) {}
    return list
  }

  fun saveHistory(history: List<HistoryItem>) {
    val jsonArray = JSONArray()
    for (item in history.take(150)) {
      val obj = JSONObject().apply {
        put("id", item.id)
        put("title", item.title)
        put("url", item.url)
        put("timestamp", item.timestamp)
      }
      jsonArray.put(obj)
    }
    prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
  }

  fun clearHistory() {
    prefs.edit().remove(KEY_HISTORY).apply()
  }

  // --- Downloads Persistence ---

  fun loadDownloads(): List<DownloadItem> {
    val jsonString = prefs.getString(KEY_DOWNLOADS, "[]") ?: "[]"
    val list = mutableListOf<DownloadItem>()
    try {
      val jsonArray = JSONArray(jsonString)
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          DownloadItem(
            id = obj.getString("id"),
            fileName = obj.getString("fileName"),
            url = obj.getString("url"),
            mimeType = obj.optString("mimeType", ""),
            contentLength = obj.optLong("contentLength", 0L),
            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
          )
        )
      }
    } catch (_: Exception) {}
    return list
  }

  fun saveDownloads(downloads: List<DownloadItem>) {
    val jsonArray = JSONArray()
    for (item in downloads.take(100)) {
      val obj = JSONObject().apply {
        put("id", item.id)
        put("fileName", item.fileName)
        put("url", item.url)
        put("mimeType", item.mimeType)
        put("contentLength", item.contentLength)
        put("timestamp", item.timestamp)
      }
      jsonArray.put(obj)
    }
    prefs.edit().putString(KEY_DOWNLOADS, jsonArray.toString()).apply()
  }

  fun clearDownloads() {
    prefs.edit().remove(KEY_DOWNLOADS).apply()
  }

  companion object {
    private const val KEY_THEME_MODE = "pref_theme_mode"
    private const val KEY_SHOW_PROGRESS_BAR = "pref_show_progress_bar"
    private const val KEY_AD_BLOCKER = "pref_ad_blocker"
    private const val KEY_EXCESSIVE_ADS = "pref_excessive_ads"
    private const val KEY_BLOCK_THIRD_PARTY_COOKIES = "pref_block_third_party_cookies"
    private const val KEY_DESKTOP_MODE = "pref_desktop_mode"

    private const val KEY_BOOKMARKS = "pref_bookmarks_data"
    private const val KEY_HISTORY = "pref_history_data"
    private const val KEY_DOWNLOADS = "pref_downloads_data"
  }
}
