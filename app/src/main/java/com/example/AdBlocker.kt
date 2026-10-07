package com.example

import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream
import java.net.URI

object AdBlocker {

  // Common high-frequency ad networks and ad tracking servers
  private val STANDARD_AD_DOMAINS = setOf(
    "doubleclick.net",
    "googlesyndication.com",
    "adservice.google.com",
    "googleads.g.doubleclick.net",
    "pagead2.googlesyndication.com",
    "pagead2.googleadservices.com",
    "adservice.google",
    "adnxs.com",
    "advertising.com",
    "rubiconproject.com",
    "pubmatic.com",
    "criteo.com",
    "outbrain.com",
    "taboola.com",
    "bidswitch.net",
    "openx.net",
    "serving-sys.com",
    "smartadserver.com",
    "casalemedia.com",
    "adroll.com",
    "scorecardresearch.com",
    "amazon-adsystem.com",
    "media.net",
    "zedo.com"
  )

  // Aggressive / excessive ad networks (pop-ups, push notifications, intrusive banners)
  private val EXCESSIVE_AD_DOMAINS = setOf(
    "exoclick.com",
    "trafficjunky.com",
    "zergnet.com",
    "mgid.com",
    "revcontent.com",
    "popads.net",
    "popcash.net",
    "propellerads.com",
    "adsterra.com",
    "adcolony.com",
    "inmobi.com",
    "unityads.unity3d.com",
    "vungle.com",
    "applovin.com",
    "chartboost.com",
    "ironsrc.com",
    "moatads.com",
    "exponential.com",
    "adblade.com",
    "clickadu.com",
    "richaudience.com",
    "yadro.ru",
    "adreactor.com"
  )

  /**
   * Evaluates if a given URL belongs to an ad server based on enabled blocker settings.
   */
  fun isAd(url: String, adBlockerEnabled: Boolean, excessiveBlockEnabled: Boolean): Boolean {
    if (!adBlockerEnabled && !excessiveBlockEnabled) {
      return false
    }

    val host = extractHost(url) ?: return false

    // Standard Ad Filtering
    if (adBlockerEnabled) {
      if (STANDARD_AD_DOMAINS.any { host == it || host.endsWith(".$it") }) {
        return true
      }
    }

    // Excessive / Aggressive Ad Filtering
    if (excessiveBlockEnabled) {
      if (EXCESSIVE_AD_DOMAINS.any { host == it || host.endsWith(".$it") }) {
        return true
      }

      val lowercaseUrl = url.lowercase()
      if (lowercaseUrl.contains("/ads/") ||
        lowercaseUrl.contains("/ad/") ||
        lowercaseUrl.contains("/advert/") ||
        lowercaseUrl.contains("/advertisement/") ||
        lowercaseUrl.contains("popunder") ||
        lowercaseUrl.contains("bannerad") ||
        lowercaseUrl.contains("/sponsor/") ||
        lowercaseUrl.contains("ad_type=") ||
        lowercaseUrl.contains("ad_unit=")
      ) {
        return true
      }
    }

    return false
  }

  fun extractHost(url: String): String? {
    return try {
      val parsed = URI(url).host
      if (!parsed.isNullOrBlank()) parsed.lowercase() else null
    } catch (_: Exception) {
      // Fallback manual parsing if URL syntax is non-standard
      try {
        val withoutScheme = url.substringAfter("://", url).substringBefore("/").substringBefore("?")
        val hostOnly = withoutScheme.substringBefore(":")
        if (hostOnly.isNotBlank()) hostOnly.lowercase() else null
      } catch (_: Exception) {
        null
      }
    }
  }

  /**
   * Generates a 200 OK empty response to immediately stop the network request.
   */
  fun createEmptyResourceResponse(): WebResourceResponse {
    return WebResourceResponse(
      "text/plain",
      "UTF-8",
      ByteArrayInputStream(ByteArray(0))
    )
  }

  /**
   * Cosmetic script injected on page load to hide ad containers from DOM.
   */
  const val COSMETIC_AD_BLOCK_JS = """
    (function() {
      try {
        var style = document.createElement('style');
        style.type = 'text/css';
        style.innerHTML = '.adsbygoogle, [id*="google_ads"], [id*="banner_ad"], .ad-container, .advertisement, [class*="sponsored"], [class*="ad-banner"], [class*="ad_wrapper"], .ad-box { display: none !important; height: 0 !important; max-height: 0 !important; visibility: hidden !important; opacity: 0 !important; pointer-events: none !important; }';
        (document.head || document.documentElement).appendChild(style);
      } catch(e) {}
    })();
  """
}
