package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testGoogleSearchUrlGeneration() {
    val searchUrl = UrlHelper.buildGoogleSearchUrl("compose webview")
    assertEquals("https://www.google.com/search?q=compose+webview", searchUrl)
  }

  @Test
  fun testHttpsDetection() {
    assertTrue(UrlHelper.isHttps("https://example.com"))
    assertFalse(UrlHelper.isHttps("http://example.com"))
  }

  @Test
  fun testAdBlockerDetection() {
    // Standard ad domain blocked when ad blocker enabled
    assertTrue(
      AdBlocker.isAd("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js", adBlockerEnabled = true, excessiveBlockEnabled = false)
    )
    assertTrue(
      AdBlocker.isAd("https://adservice.google.com/adsid/integrator.js", adBlockerEnabled = true, excessiveBlockEnabled = false)
    )

    // Allowed when ad blocker disabled
    assertFalse(
      AdBlocker.isAd("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js", adBlockerEnabled = false, excessiveBlockEnabled = false)
    )

    // Excessive ad domain blocked when excessive ads enabled
    assertTrue(
      AdBlocker.isAd("https://exoclick.com/banner.js", adBlockerEnabled = false, excessiveBlockEnabled = true)
    )

    // Normal non-ad domain is not blocked
    assertFalse(
      AdBlocker.isAd("https://wikipedia.org/wiki/Main_Page", adBlockerEnabled = true, excessiveBlockEnabled = true)
    )
    assertFalse(
      AdBlocker.isAd("https://github.com/torvalds/linux", adBlockerEnabled = true, excessiveBlockEnabled = true)
    )
  }
}
