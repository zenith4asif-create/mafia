package com.example

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BrowserUiState(
  val tabs: List<BrowserTab> = listOf(BrowserTab(id = "default_tab", title = "New Tab", url = "")),
  val activeTabId: String = "default_tab",
  val urlInput: String = "",
  val currentUrl: String = "",
  val pageTitle: String = "",
  val isLoading: Boolean = false,
  val isRefreshing: Boolean = false,
  val progress: Int = 0,
  val canGoBack: Boolean = false,
  val canGoForward: Boolean = false,
  val errorMessage: String? = null,
  val isOffline: Boolean = false,
  val isInitialHome: Boolean = true,
  val isMenuOpen: Boolean = false,
  val activeSheet: ActiveSheet = ActiveSheet.NONE,
  val settings: BrowserSettings = BrowserSettings(),
  val bookmarks: List<BookmarkItem> = emptyList(),
  val history: List<HistoryItem> = emptyList(),
  val downloads: List<DownloadItem> = emptyList()
) {
  val activeTab: BrowserTab
    get() = tabs.find { it.id == activeTabId } ?: tabs.firstOrNull() ?: BrowserTab()

  val isIncognito: Boolean
    get() = activeTab.isIncognito

  val isBookmarked: Boolean
    get() = bookmarks.any { it.url == currentUrl && currentUrl.isNotBlank() }
}

sealed interface WebViewCommand {
  data class LoadUrl(val url: String) : WebViewCommand
  data object Reload : WebViewCommand
  data object StopLoading : WebViewCommand
  data object GoBack : WebViewCommand
  data object GoForward : WebViewCommand
  data class SetDesktopMode(val enabled: Boolean) : WebViewCommand
  data class UpdateCookiePolicy(val blockThirdPartyCookies: Boolean) : WebViewCommand
  data class ClearWebData(val clearCookies: Boolean, val clearCache: Boolean) : WebViewCommand
}

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = BrowserRepository(application.applicationContext)

  private val _uiState = MutableStateFlow(
    BrowserUiState(
      settings = repository.loadSettings(),
      bookmarks = repository.loadBookmarks(),
      history = repository.loadHistory(),
      downloads = repository.loadDownloads()
    )
  )
  val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

  private val _commands = MutableSharedFlow<WebViewCommand>(extraBufferCapacity = 10)
  val commands: SharedFlow<WebViewCommand> = _commands.asSharedFlow()

  init {
    monitorNetworkState(application)
  }

  private fun monitorNetworkState(app: Application) {
    val cm = app.getSystemService(ConnectivityManager::class.java) ?: return
    val networkRequest = NetworkRequest.Builder()
      .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
      .build()

    try {
      val isConnected = cm.activeNetwork?.let { nw ->
        cm.getNetworkCapabilities(nw)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
      } ?: false
      _uiState.update { it.copy(isOffline = !isConnected) }

      cm.registerNetworkCallback(
        networkRequest,
        object : ConnectivityManager.NetworkCallback() {
          override fun onAvailable(network: Network) {
            _uiState.update { it.copy(isOffline = false) }
          }

          override fun onLost(network: Network) {
            _uiState.update { it.copy(isOffline = true) }
          }
        }
      )
    } catch (_: Exception) {}
  }

  // --- Step-by-Step Navigation & Omnibox ---

  fun onUrlInputChanged(newText: String) {
    _uiState.update { it.copy(urlInput = newText) }
  }

  fun submitInput(input: String = _uiState.value.urlInput) {
    val destinationUrl = UrlHelper.processInput(input)
    val currentTab = _uiState.value.activeTab

    _uiState.update { state ->
      val updatedTabs = state.tabs.map { tab ->
        if (tab.id == currentTab.id) tab.copy(url = destinationUrl) else tab
      }
      state.copy(
        tabs = updatedTabs,
        urlInput = destinationUrl,
        errorMessage = null,
        isInitialHome = false,
        isMenuOpen = false
      )
    }
    viewModelScope.launch {
      _commands.emit(WebViewCommand.LoadUrl(destinationUrl))
    }
  }

  fun loadUrlDirectly(url: String) {
    val currentTab = _uiState.value.activeTab
    _uiState.update { state ->
      val updatedTabs = state.tabs.map { tab ->
        if (tab.id == currentTab.id) tab.copy(url = url) else tab
      }
      state.copy(
        tabs = updatedTabs,
        urlInput = url,
        errorMessage = null,
        isInitialHome = false,
        isMenuOpen = false
      )
    }
    viewModelScope.launch {
      _commands.emit(WebViewCommand.LoadUrl(url))
    }
  }

  fun onHomeClicked() {
    val currentTab = _uiState.value.activeTab
    _uiState.update { state ->
      val updatedTabs = state.tabs.map { tab ->
        if (tab.id == currentTab.id) tab.copy(url = "", title = "New Tab") else tab
      }
      state.copy(
        tabs = updatedTabs,
        urlInput = "",
        currentUrl = "",
        pageTitle = "",
        isLoading = false,
        progress = 0,
        errorMessage = null,
        isInitialHome = true,
        isMenuOpen = false
      )
    }
    viewModelScope.launch {
      _commands.emit(WebViewCommand.LoadUrl("about:blank"))
    }
  }

  /**
   * Step-by-step back navigation:
   * 1. Closes any open sheet or dropdown menu
   * 2. Navigates back one step in WebView history if canGoBack
   * 3. If at the root of web history and not on Home, returns to Home (NTP)
   * 4. Returns boolean indicating if back navigation was handled internally.
   */
  fun handleStepBack(): Boolean {
    val state = _uiState.value

    if (state.activeSheet != ActiveSheet.NONE) {
      closeActiveSheet()
      return true
    }

    if (state.isMenuOpen) {
      closeMenu()
      return true
    }

    if (state.canGoBack) {
      viewModelScope.launch {
        _commands.emit(WebViewCommand.GoBack)
      }
      return true
    }

    if (!state.isInitialHome) {
      onHomeClicked()
      return true
    }

    if (state.tabs.size > 1) {
      closeTab(state.activeTabId)
      return true
    }

    return false
  }

  fun onBackClicked() {
    handleStepBack()
  }

  fun onForwardClicked() {
    viewModelScope.launch {
      _commands.emit(WebViewCommand.GoForward)
    }
  }

  fun onReloadClicked() {
    val state = _uiState.value
    viewModelScope.launch {
      if (state.isLoading) {
        _commands.emit(WebViewCommand.StopLoading)
      } else {
        _uiState.update { it.copy(errorMessage = null) }
        _commands.emit(WebViewCommand.Reload)
      }
    }
  }

  fun onPullToRefresh() {
    _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
    viewModelScope.launch {
      _commands.emit(WebViewCommand.Reload)
    }
  }

  // --- WebView Event Callbacks ---

  fun onPageStarted(url: String) {
    _uiState.update { state ->
      state.copy(
        currentUrl = url,
        urlInput = if (url == "about:blank") "" else url,
        isLoading = true,
        errorMessage = null,
        isInitialHome = (url == "about:blank" || url.isEmpty())
      )
    }
  }

  fun onPageFinished(url: String, canGoBack: Boolean, canGoForward: Boolean) {
    val currentTab = _uiState.value.activeTab
    val pageTitle = _uiState.value.pageTitle.ifBlank { url }

    if (!currentTab.isIncognito && url != "about:blank" && url.isNotBlank()) {
      recordHistory(pageTitle, url)
    }

    _uiState.update { state ->
      val updatedTabs = state.tabs.map { tab ->
        if (tab.id == currentTab.id) {
          tab.copy(
            url = url,
            title = pageTitle,
            canGoBack = canGoBack,
            canGoForward = canGoForward
          )
        } else tab
      }
      state.copy(
        tabs = updatedTabs,
        currentUrl = url,
        urlInput = if (url == "about:blank") "" else url,
        isLoading = false,
        isRefreshing = false,
        progress = 100,
        canGoBack = canGoBack,
        canGoForward = canGoForward,
        isInitialHome = (url == "about:blank" || url.isEmpty())
      )
    }
  }

  fun onProgressChanged(progress: Int) {
    _uiState.update {
      it.copy(
        progress = progress,
        isLoading = progress < 100
      )
    }
  }

  fun onReceivedTitle(title: String) {
    _uiState.update { it.copy(pageTitle = title) }
  }

  fun onReceivedError(description: String) {
    val isNetError = description.contains("ERR_INTERNET_DISCONNECTED", ignoreCase = true) ||
      description.contains("ERR_NAME_NOT_RESOLVED", ignoreCase = true) ||
      description.contains("net::ERR", ignoreCase = true)

    _uiState.update {
      it.copy(
        isLoading = false,
        isRefreshing = false,
        errorMessage = description,
        isOffline = if (isNetError) true else it.isOffline
      )
    }
  }

  fun clearError() {
    _uiState.update { it.copy(errorMessage = null) }
  }

  fun retryConnection() {
    _uiState.update { it.copy(isOffline = false, errorMessage = null) }
    onReloadClicked()
  }

  // --- O2 3-Dots Menu & Sheets ---

  fun toggleMenu() {
    _uiState.update { it.copy(isMenuOpen = !it.isMenuOpen) }
  }

  fun closeMenu() {
    _uiState.update { it.copy(isMenuOpen = false) }
  }

  fun openSheet(sheet: ActiveSheet) {
    _uiState.update { it.copy(activeSheet = sheet, isMenuOpen = false) }
  }

  fun closeActiveSheet() {
    _uiState.update { it.copy(activeSheet = ActiveSheet.NONE) }
  }

  // --- Multi-Tabs Management ---

  fun createTab(isIncognito: Boolean = false, url: String = "") {
    val newTab = BrowserTab(
      title = if (isIncognito) "Incognito Tab" else "New Tab",
      url = url,
      isIncognito = isIncognito
    )
    _uiState.update { state ->
      state.copy(
        tabs = state.tabs + newTab,
        activeTabId = newTab.id,
        currentUrl = url,
        urlInput = url,
        isInitialHome = url.isBlank(),
        activeSheet = ActiveSheet.NONE,
        isMenuOpen = false
      )
    }
    viewModelScope.launch {
      _commands.emit(WebViewCommand.LoadUrl(if (url.isBlank()) "about:blank" else url))
    }
  }

  fun switchTab(tabId: String) {
    val targetTab = _uiState.value.tabs.find { it.id == tabId } ?: return
    _uiState.update { state ->
      state.copy(
        activeTabId = tabId,
        currentUrl = targetTab.url,
        urlInput = if (targetTab.url == "about:blank") "" else targetTab.url,
        pageTitle = targetTab.title,
        canGoBack = targetTab.canGoBack,
        canGoForward = targetTab.canGoForward,
        isInitialHome = targetTab.url.isBlank() || targetTab.url == "about:blank",
        activeSheet = ActiveSheet.NONE,
        isMenuOpen = false
      )
    }
    viewModelScope.launch {
      _commands.emit(
        WebViewCommand.LoadUrl(if (targetTab.url.isBlank()) "about:blank" else targetTab.url)
      )
    }
  }

  fun closeTab(tabId: String) {
    val currentTabs = _uiState.value.tabs
    if (currentTabs.size <= 1) {
      createTab(isIncognito = false, url = "")
      return
    }

    val updatedTabs = currentTabs.filterNot { it.id == tabId }
    val newActiveId = if (_uiState.value.activeTabId == tabId) {
      updatedTabs.last().id
    } else {
      _uiState.value.activeTabId
    }

    _uiState.update { it.copy(tabs = updatedTabs, activeTabId = newActiveId) }
    switchTab(newActiveId)
  }

  // --- Desktop Mode ---

  fun toggleDesktopMode() {
    val newMode = !_uiState.value.settings.desktopMode
    val updated = _uiState.value.settings.copy(desktopMode = newMode)
    repository.saveSettings(updated)
    _uiState.update { it.copy(settings = updated, isMenuOpen = false) }
    viewModelScope.launch {
      _commands.emit(WebViewCommand.SetDesktopMode(newMode))
      _commands.emit(WebViewCommand.Reload)
    }
  }

  // --- Bookmarks Management ---

  fun toggleBookmark() {
    val currentUrl = _uiState.value.currentUrl
    if (currentUrl.isBlank() || currentUrl == "about:blank") return

    val currentTitle = _uiState.value.pageTitle.ifBlank { currentUrl }
    val existing = _uiState.value.bookmarks.find { it.url == currentUrl }

    val updatedList = if (existing != null) {
      _uiState.value.bookmarks.filterNot { it.id == existing.id }
    } else {
      _uiState.value.bookmarks + BookmarkItem(title = currentTitle, url = currentUrl)
    }

    repository.saveBookmarks(updatedList)
    _uiState.update { it.copy(bookmarks = updatedList, isMenuOpen = false) }
  }

  fun deleteBookmark(id: String) {
    val updated = _uiState.value.bookmarks.filterNot { it.id == id }
    repository.saveBookmarks(updated)
    _uiState.update { it.copy(bookmarks = updated) }
  }

  // --- History Management ---

  private fun recordHistory(title: String, url: String) {
    val existing = _uiState.value.history.firstOrNull()
    if (existing?.url == url) return

    val newItem = HistoryItem(title = title, url = url)
    val updated = listOf(newItem) + _uiState.value.history.take(149)
    repository.saveHistory(updated)
    _uiState.update { it.copy(history = updated) }
  }

  fun deleteHistoryItem(id: String) {
    val updated = _uiState.value.history.filterNot { it.id == id }
    repository.saveHistory(updated)
    _uiState.update { it.copy(history = updated) }
  }

  fun clearAllHistory() {
    repository.clearHistory()
    _uiState.update { it.copy(history = emptyList()) }
  }

  // --- Downloads Management ---

  fun onDownloadRequested(fileName: String, url: String, mimeType: String, size: Long) {
    val item = DownloadItem(
      fileName = fileName,
      url = url,
      mimeType = mimeType,
      contentLength = size
    )
    val updated = listOf(item) + _uiState.value.downloads
    repository.saveDownloads(updated)
    _uiState.update { it.copy(downloads = updated) }
  }

  fun clearAllDownloads() {
    repository.clearDownloads()
    _uiState.update { it.copy(downloads = emptyList()) }
  }

  // --- Delete Browsing Data ---

  fun deleteBrowsingData(
    clearHistory: Boolean,
    clearCookies: Boolean,
    clearCache: Boolean,
    clearTabs: Boolean
  ) {
    if (clearHistory) {
      clearAllHistory()
    }
    if (clearTabs) {
      _uiState.update {
        it.copy(
          tabs = listOf(BrowserTab(title = "New Tab", url = "")),
          activeTabId = "default_tab",
          currentUrl = "",
          urlInput = "",
          isInitialHome = true
        )
      }
    }
    viewModelScope.launch {
      _commands.emit(WebViewCommand.ClearWebData(clearCookies = clearCookies, clearCache = clearCache))
      _commands.emit(WebViewCommand.LoadUrl("about:blank"))
    }
    closeActiveSheet()
  }

  // --- Settings ---

  fun updateThemeMode(themeMode: ThemeMode) {
    val updated = _uiState.value.settings.copy(themeMode = themeMode)
    repository.saveSettings(updated)
    _uiState.update { it.copy(settings = updated) }
  }

  fun updateShowProgressBar(show: Boolean) {
    val updated = _uiState.value.settings.copy(showProgressBar = show)
    repository.saveSettings(updated)
    _uiState.update { it.copy(settings = updated) }
  }

  fun updateAdBlocker(enabled: Boolean) {
    val updated = _uiState.value.settings.copy(adBlockerEnabled = enabled)
    repository.saveSettings(updated)
    _uiState.update { it.copy(settings = updated) }
  }

  fun updateExcessiveAdsBlock(enabled: Boolean) {
    val updated = _uiState.value.settings.copy(excessiveAdsBlockEnabled = enabled)
    repository.saveSettings(updated)
    _uiState.update { it.copy(settings = updated) }
  }

  fun updateBlockThirdPartyCookies(block: Boolean) {
    val updated = _uiState.value.settings.copy(blockThirdPartyCookies = block)
    repository.saveSettings(updated)
    _uiState.update { it.copy(settings = updated) }
    viewModelScope.launch {
      _commands.emit(WebViewCommand.UpdateCookiePolicy(block))
    }
  }
}
