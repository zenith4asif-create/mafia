package com.example

import android.util.Patterns
import java.net.URLEncoder

object UrlHelper {

  private const val GOOGLE_SEARCH_URL = "https://www.google.com/search?q="
  const val DEFAULT_HOME_URL = "https://www.google.com"

  /**
   * Evaluates the user input and decides whether to navigate to a direct URL
   * or perform a Google search.
   */
  fun processInput(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) {
      return DEFAULT_HOME_URL
    }

    // Direct scheme already provided
    if (trimmed.startsWith("http://", ignoreCase = true) ||
      trimmed.startsWith("https://", ignoreCase = true) ||
      trimmed.startsWith("about:", ignoreCase = true)
    ) {
      return trimmed
    }

    // If query contains whitespace, it's definitely a search query
    if (trimmed.contains(" ") || !trimmed.contains(".")) {
      return buildGoogleSearchUrl(trimmed)
    }

    // Special cases: localhost or numeric IP with or without port
    if (trimmed.startsWith("localhost", ignoreCase = true) ||
      trimmed.matches(Regex("^(\\d{1,3}\\.){3}\\d{1,3}(:\\d+)?(/.*)?$"))
    ) {
      return "http://$trimmed"
    }

    // Standard web URL pattern (e.g. google.com, en.wikipedia.org/wiki/Android)
    if (Patterns.WEB_URL.matcher(trimmed).matches() ||
      trimmed.matches(Regex("^[a-zA-Z0-9][-a-zA-Z0-9]*(\\.[a-zA-Z0-9][-a-zA-Z0-9]*)+(:\\d+)?(/.*)?$"))
    ) {
      return "https://$trimmed"
    }

    // Fallback to Google search
    return buildGoogleSearchUrl(trimmed)
  }

  fun buildGoogleSearchUrl(query: String): String {
    val encodedQuery = try {
      URLEncoder.encode(query.trim(), "UTF-8")
    } catch (_: Exception) {
      query.trim().replace(" ", "+")
    }
    return "$GOOGLE_SEARCH_URL$encodedQuery"
  }

  fun isHttps(url: String): Boolean {
    return url.startsWith("https://", ignoreCase = true)
  }
}
