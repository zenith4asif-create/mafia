package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context including app and developer name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("O2 Browser Lite", appName)

    val developerName = context.getString(R.string.developer_name)
    assertEquals("ASIF ANSARI", developerName)

    val developedBy = context.getString(R.string.developed_by)
    assertEquals("Developed by ASIF ANSARI • CEO of O2 Technologies", developedBy)
  }

  @Test
  fun `verify url vs search parsing`() {
    val searchResult = UrlHelper.processInput("what is android")
    assertTrue(searchResult.startsWith("https://www.google.com/search?q="))

    val singleWordResult = UrlHelper.processInput("weather")
    assertTrue(singleWordResult.startsWith("https://www.google.com/search?q=weather"))

    assertEquals("https://github.com", UrlHelper.processInput("https://github.com"))
    assertEquals("http://example.com", UrlHelper.processInput("http://example.com"))
    assertEquals("https://wikipedia.org", UrlHelper.processInput("wikipedia.org"))
  }

  @Test
  fun `verify browser repository persistence for settings, bookmarks and history`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = BrowserRepository(context)

    val customSettings = BrowserSettings(
      themeMode = ThemeMode.DARK,
      showProgressBar = false,
      adBlockerEnabled = true,
      excessiveAdsBlockEnabled = true,
      blockThirdPartyCookies = true,
      desktopMode = true
    )

    repo.saveSettings(customSettings)
    val loadedSettings = repo.loadSettings()

    assertEquals(ThemeMode.DARK, loadedSettings.themeMode)
    assertFalse(loadedSettings.showProgressBar)
    assertTrue(loadedSettings.desktopMode)
    assertTrue(loadedSettings.adBlockerEnabled)
    assertTrue(loadedSettings.blockThirdPartyCookies)

    // Verify bookmarks
    val testBookmarks = listOf(BookmarkItem(title = "GitHub", url = "https://github.com"))
    repo.saveBookmarks(testBookmarks)
    val loadedBookmarks = repo.loadBookmarks()
    assertEquals(1, loadedBookmarks.size)
    assertEquals("https://github.com", loadedBookmarks[0].url)

    // Verify history
    val testHistory = listOf(HistoryItem(title = "Kotlin", url = "https://kotlinlang.org"))
    repo.saveHistory(testHistory)
    val loadedHistory = repo.loadHistory()
    assertEquals(1, loadedHistory.size)
    assertEquals("https://kotlinlang.org", loadedHistory[0].url)
  }
}
