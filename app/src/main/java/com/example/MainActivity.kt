package com.example

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebViewDatabase
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.O2BlueDark
import com.example.ui.theme.O2BlueLight
import com.example.ui.theme.O2OmniboxDark
import com.example.ui.theme.O2OmniboxLight
import com.example.ui.theme.O2ShortcutBgDark
import com.example.ui.theme.O2ShortcutBgLight
import com.example.ui.theme.O2SurfaceDark
import com.example.ui.theme.O2SurfaceLight
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private var webViewInstance: WebView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val viewModel: BrowserViewModel = viewModel()
      val uiState by viewModel.uiState.collectAsState()

      MyApplicationTheme(themeMode = uiState.settings.themeMode) {
        BrowserScreen(
          viewModel = viewModel,
          onAttachWebView = { webViewInstance = it }
        )
      }
    }
  }

  override fun onPause() {
    super.onPause()
    webViewInstance?.onPause()
  }

  override fun onResume() {
    super.onResume()
    webViewInstance?.onResume()
  }

  override fun onDestroy() {
    super.onDestroy()
    webViewInstance?.let {
      it.stopLoading()
      (it.parent as? ViewGroup)?.removeView(it)
      it.destroy()
    }
    webViewInstance = null
  }
}

private const val DESKTOP_USER_AGENT =
  "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
  viewModel: BrowserViewModel,
  onAttachWebView: (WebView) -> Unit,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current
  var webViewRef by remember { mutableStateOf<WebView?>(null) }

  val isDark = when (uiState.settings.themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.DARK -> true
    ThemeMode.LIGHT -> false
  }

  val primaryBlue = if (isDark) O2BlueDark else O2BlueLight
  val surfaceColor = if (uiState.isIncognito) Color(0xFF1F1F1F) else if (isDark) O2SurfaceDark else O2SurfaceLight
  val omniboxColor = if (uiState.isIncognito) Color(0xFF2B2B2B) else if (isDark) O2OmniboxDark else O2OmniboxLight

  // STEP-BY-STEP BACK NAVIGATION
  BackHandler(enabled = true) {
    val handled = viewModel.handleStepBack()
    if (!handled) {
      (context as? ComponentActivity)?.finish()
    }
  }

  // Handle commands sent from ViewModel to WebView
  LaunchedEffect(viewModel) {
    viewModel.commands.collect { cmd ->
      when (cmd) {
        is WebViewCommand.LoadUrl -> webViewRef?.loadUrl(cmd.url)
        WebViewCommand.Reload -> webViewRef?.reload()
        WebViewCommand.StopLoading -> webViewRef?.stopLoading()
        WebViewCommand.GoBack -> if (webViewRef?.canGoBack() == true) webViewRef?.goBack()
        WebViewCommand.GoForward -> if (webViewRef?.canGoForward() == true) webViewRef?.goForward()
        is WebViewCommand.SetDesktopMode -> {
          webViewRef?.settings?.let { s ->
            s.userAgentString = if (cmd.enabled) DESKTOP_USER_AGENT else null
            s.useWideViewPort = true
            s.loadWithOverviewMode = true
          }
        }
        is WebViewCommand.UpdateCookiePolicy -> {
          webViewRef?.let { wv ->
            CookieManager.getInstance().setAcceptThirdPartyCookies(wv, !cmd.blockThirdPartyCookies)
          }
        }
        is WebViewCommand.ClearWebData -> {
          if (cmd.clearCookies) {
            CookieManager.getInstance().removeAllCookies(null)
          }
          if (cmd.clearCache) {
            webViewRef?.clearCache(true)
            WebViewDatabase.getInstance(context).clearHttpAuthUsernamePassword()
          }
          Toast.makeText(context, context.getString(R.string.data_cleared_success), Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(surfaceColor)
          .padding(top = statusBarPadding)
      ) {
        // O2 Browser Top Toolbar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Home / Step-back button
          IconButton(
            onClick = {
              if (uiState.canGoBack) {
                viewModel.onBackClicked()
              } else {
                viewModel.onHomeClicked()
              }
            },
            modifier = Modifier.size(40.dp).testTag("home_button")
          ) {
            Icon(
              imageVector = if (uiState.canGoBack) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Home,
              contentDescription = stringResource(R.string.home),
              tint = if (uiState.isIncognito) Color(0xFFE8EAED) else MaterialTheme.colorScheme.onSurface
            )
          }

          // O2 Omnibox
          OutlinedTextField(
            value = uiState.urlInput,
            onValueChange = { viewModel.onUrlInputChanged(it) },
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .padding(horizontal = 4.dp)
              .testTag("address_input"),
            placeholder = {
              Text(
                text = stringResource(R.string.search_or_enter_url),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            },
            singleLine = true,
            leadingIcon = {
              val isSecure = UrlHelper.isHttps(uiState.currentUrl)
              Icon(
                imageVector = if (uiState.isIncognito) Icons.Default.VisibilityOff
                else if (isSecure) Icons.Default.Lock else Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = if (uiState.isIncognito) Color.White
                else if (isSecure) primaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
              )
            },
            trailingIcon = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (uiState.settings.desktopMode) {
                  Icon(
                    imageVector = Icons.Default.DesktopWindows,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp).padding(end = 4.dp),
                    tint = primaryBlue
                  )
                }

                if (uiState.urlInput.isNotEmpty()) {
                  IconButton(
                    onClick = { viewModel.onUrlInputChanged("") },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear), modifier = Modifier.size(16.dp))
                  }
                }
              }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(
              onGo = {
                focusManager.clearFocus()
                keyboardController?.hide()
                viewModel.submitInput()
              }
            ),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = omniboxColor,
              unfocusedContainerColor = omniboxColor,
              focusedBorderColor = primaryBlue,
              unfocusedBorderColor = Color.Transparent
            )
          )

          // Tab Switcher Box [ N ]
          Surface(
            modifier = Modifier
              .padding(horizontal = 4.dp)
              .clip(RoundedCornerShape(6.dp))
              .clickable { viewModel.openSheet(ActiveSheet.TABS) }
              .testTag("tabs_button"),
            shape = RoundedCornerShape(6.dp),
            color = Color.Transparent,
            border = BorderStroke(1.5.dp, if (uiState.isIncognito) Color.White else MaterialTheme.colorScheme.onSurface)
          ) {
            Box(
              modifier = Modifier.size(26.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${uiState.tabs.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (uiState.isIncognito) Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }

          // 3-Dots Menu Button
          Box {
            IconButton(
              onClick = { viewModel.toggleMenu() },
              modifier = Modifier.size(40.dp).testTag("menu_button")
            ) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu",
                tint = if (uiState.isIncognito) Color.White else MaterialTheme.colorScheme.onSurface
              )
            }

            // O2 Dropdown Menu
            O2DropdownMenu(
              expanded = uiState.isMenuOpen,
              onDismiss = { viewModel.closeMenu() },
              canGoForward = uiState.canGoForward,
              isBookmarked = uiState.isBookmarked,
              desktopMode = uiState.settings.desktopMode,
              onForward = { viewModel.onForwardClicked() },
              onBookmark = { viewModel.toggleBookmark() },
              onReload = { viewModel.onReloadClicked() },
              onNewTab = { viewModel.createTab(isIncognito = false) },
              onNewIncognitoTab = { viewModel.createTab(isIncognito = true) },
              onHistory = { viewModel.openSheet(ActiveSheet.HISTORY) },
              onDeleteData = { viewModel.openSheet(ActiveSheet.CLEAR_DATA) },
              onDownloads = { viewModel.openSheet(ActiveSheet.DOWNLOADS) },
              onBookmarks = { viewModel.openSheet(ActiveSheet.BOOKMARKS) },
              onToggleDesktop = { viewModel.toggleDesktopMode() },
              onSettings = { viewModel.openSheet(ActiveSheet.SETTINGS) },
              onAbout = { viewModel.openSheet(ActiveSheet.ABOUT) },
              onHelp = { viewModel.openSheet(ActiveSheet.HELP) },
              isDark = isDark
            )
          }
        }

        // Top Loading Progress Bar
        if (uiState.settings.showProgressBar) {
          AnimatedVisibility(visible = uiState.isLoading, enter = fadeIn(), exit = fadeOut()) {
            LinearProgressIndicator(
              progress = { (uiState.progress.coerceIn(0, 100)) / 100f },
              modifier = Modifier.fillMaxWidth().height(2.5.dp),
              color = primaryBlue,
              trackColor = Color.Transparent
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("webview_container")
    ) {
      if (uiState.isOffline) {
        OfflineCartoonAndGameScreen(
          onRetry = { viewModel.retryConnection() },
          accentColor = primaryBlue,
          isDark = isDark
        )
      } else {
        PullToRefreshBox(
          isRefreshing = uiState.isRefreshing,
          onRefresh = { viewModel.onPullToRefresh() },
          modifier = Modifier.fillMaxSize()
        ) {
          AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
              WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                settings.apply {
                  javaScriptEnabled = true
                  domStorageEnabled = true
                  useWideViewPort = true
                  loadWithOverviewMode = true
                  setSupportZoom(true)
                  builtInZoomControls = true
                  displayZoomControls = false
                  cacheMode = WebSettings.LOAD_DEFAULT
                  mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                  if (uiState.settings.desktopMode) {
                    userAgentString = DESKTOP_USER_AGENT
                  }
                }

                // Cookie support
                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, !uiState.settings.blockThirdPartyCookies)

                // Download Listener
                setDownloadListener { url, _, contentDisposition, mimeType, contentLength ->
                  val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
                  viewModel.onDownloadRequested(fileName, url, mimeType, contentLength)
                  Toast.makeText(ctx, "Download: $fileName", Toast.LENGTH_SHORT).show()
                }

                webViewClient = object : WebViewClient() {
                  override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    if (url.startsWith("http://") || url.startsWith("https://")) return false
                    try {
                      ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (_: ActivityNotFoundException) {}
                    return true
                  }

                  override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                    val requestUrl = request?.url?.toString() ?: return super.shouldInterceptRequest(view, request)
                    if (AdBlocker.isAd(
                        url = requestUrl,
                        adBlockerEnabled = uiState.settings.adBlockerEnabled,
                        excessiveBlockEnabled = uiState.settings.excessiveAdsBlockEnabled
                      )
                    ) {
                      return AdBlocker.createEmptyResourceResponse()
                    }
                    return super.shouldInterceptRequest(view, request)
                  }

                  override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    url?.let { viewModel.onPageStarted(it) }
                  }

                  override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let {
                      viewModel.onPageFinished(it, view?.canGoBack() == true, view?.canGoForward() == true)
                    }
                    if (uiState.settings.excessiveAdsBlockEnabled) {
                      view?.evaluateJavascript(AdBlocker.COSMETIC_AD_BLOCK_JS, null)
                    }
                  }

                  override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true) {
                      viewModel.onReceivedError(error?.description?.toString() ?: "Page loading failed")
                    }
                  }
                }

                webChromeClient = object : WebChromeClient() {
                  override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    viewModel.onProgressChanged(newProgress)
                  }

                  override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    title?.let { viewModel.onReceivedTitle(it) }
                  }
                }

                webViewRef = this
                onAttachWebView(this)
              }
            },
            update = {}
          )
        }

        // O2 Browser Home Screen
        if (uiState.isInitialHome) {
          if (uiState.isIncognito) {
            O2IncognitoHomeScreen(
              onSearchSubmit = { q ->
                focusManager.clearFocus()
                keyboardController?.hide()
                viewModel.submitInput(q)
              },
              blockThirdPartyCookies = uiState.settings.blockThirdPartyCookies,
              onToggleCookies = { viewModel.updateBlockThirdPartyCookies(it) }
            )
          } else {
            O2HomeScreen(
              onSearchSubmit = { q ->
                focusManager.clearFocus()
                keyboardController?.hide()
                viewModel.submitInput(q)
              },
              onShortcutClicked = { u ->
                focusManager.clearFocus()
                keyboardController?.hide()
                viewModel.loadUrlDirectly(u)
              },
              onOpenAbout = { viewModel.openSheet(ActiveSheet.ABOUT) },
              isDark = isDark,
              primaryBlue = primaryBlue
            )
          }
        }
      }

      // Sheets Routing
      when (uiState.activeSheet) {
        ActiveSheet.SETTINGS -> {
          O2SettingsSheet(
            settings = uiState.settings,
            onDismiss = { viewModel.closeActiveSheet() },
            onThemeChange = { viewModel.updateThemeMode(it) },
            onToggleProgressBar = { viewModel.updateShowProgressBar(it) },
            onToggleAdBlocker = { viewModel.updateAdBlocker(it) },
            onToggleExcessiveAds = { viewModel.updateExcessiveAdsBlock(it) },
            onToggleThirdPartyCookies = { viewModel.updateBlockThirdPartyCookies(it) },
            onOpenAbout = { viewModel.openSheet(ActiveSheet.ABOUT) },
            onOpenHelp = { viewModel.openSheet(ActiveSheet.HELP) },
            isDark = isDark
          )
        }
        ActiveSheet.ABOUT -> {
          O2AboutSheet(
            onDismiss = { viewModel.closeActiveSheet() },
            onOpenHelp = { viewModel.openSheet(ActiveSheet.HELP) },
            isDark = isDark
          )
        }
        ActiveSheet.HELP -> {
          O2HelpSheet(
            onDismiss = { viewModel.closeActiveSheet() },
            isDark = isDark
          )
        }
        ActiveSheet.TABS -> {
          O2TabsManagerSheet(
            tabs = uiState.tabs,
            activeTabId = uiState.activeTabId,
            onSwitchTab = { viewModel.switchTab(it) },
            onCloseTab = { viewModel.closeTab(it) },
            onNewTab = { viewModel.createTab(isIncognito = false) },
            onNewIncognitoTab = { viewModel.createTab(isIncognito = true) },
            onDismiss = { viewModel.closeActiveSheet() },
            isDark = isDark
          )
        }
        ActiveSheet.BOOKMARKS -> {
          O2BookmarksSheet(
            bookmarks = uiState.bookmarks,
            onSelectBookmark = {
              viewModel.closeActiveSheet()
              viewModel.loadUrlDirectly(it)
            },
            onDeleteBookmark = { viewModel.deleteBookmark(it) },
            onDismiss = { viewModel.closeActiveSheet() },
            isDark = isDark
          )
        }
        ActiveSheet.HISTORY -> {
          O2HistorySheet(
            history = uiState.history,
            onSelectHistory = {
              viewModel.closeActiveSheet()
              viewModel.loadUrlDirectly(it)
            },
            onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
            onClearAll = { viewModel.clearAllHistory() },
            onDismiss = { viewModel.closeActiveSheet() },
            isDark = isDark
          )
        }
        ActiveSheet.DOWNLOADS -> {
          O2DownloadsSheet(
            downloads = uiState.downloads,
            onSelectDownload = {
              viewModel.closeActiveSheet()
              viewModel.loadUrlDirectly(it)
            },
            onClearDownloads = { viewModel.clearAllDownloads() },
            onDismiss = { viewModel.closeActiveSheet() },
            isDark = isDark
          )
        }
        ActiveSheet.CLEAR_DATA -> {
          O2ClearDataSheet(
            onConfirmClear = { h, c, ca, t ->
              viewModel.deleteBrowsingData(clearHistory = h, clearCookies = c, clearCache = ca, clearTabs = t)
            },
            onDismiss = { viewModel.closeActiveSheet() },
            isDark = isDark
          )
        }
        ActiveSheet.NONE -> {}
      }
    }
  }

  DisposableEffect(Unit) {
    onDispose { webViewRef = null }
  }
}

/**
 * Authentic O2 Browser Home Screen featuring O2 Logo, ASIF ANSARI, and 70% Speed Highlight
 */
@Composable
fun O2HomeScreen(
  onSearchSubmit: (String) -> Unit,
  onShortcutClicked: (String) -> Unit,
  onOpenAbout: () -> Unit,
  isDark: Boolean,
  primaryBlue: Color,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  val bgColor = if (isDark) O2SurfaceDark else O2SurfaceLight
  val omniboxBg = if (isDark) O2OmniboxDark else O2OmniboxLight

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(bgColor)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 20.dp, vertical = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Top
  ) {
    Spacer(modifier = Modifier.height(24.dp))

    // 1. O2 Logo Emblem
    O2Logo(modifier = Modifier.size(76.dp))

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "O2 Browser Lite",
      fontSize = 22.sp,
      fontWeight = FontWeight.Black,
      color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(4.dp))

    // 70% Faster tag & ASIF ANSARI CEO Card
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = primaryBlue.copy(alpha = 0.12f),
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .clickable { onOpenAbout() }
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Speed, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "70% Faster Browsing • ASIF ANSARI, CEO",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = primaryBlue
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 2. O2 Search Omnibox on NTP
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp),
      shape = RoundedCornerShape(26.dp),
      color = omniboxBg,
      shadowElevation = if (isDark) 1.dp else 2.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "O₂",
          fontSize = 17.sp,
          fontWeight = FontWeight.Black,
          color = primaryBlue
        )

        Spacer(modifier = Modifier.width(12.dp))

        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text(stringResource(R.string.search_or_enter_url), fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
          singleLine = true,
          modifier = Modifier.weight(1f).testTag("home_search_input"),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Search),
          keyboardActions = KeyboardActions(
            onSearch = {
              if (searchQuery.isNotBlank()) onSearchSubmit(searchQuery)
            }
          ),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
          )
        )

        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // 3. Most Visited Shortcuts Grid (4x2)
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        O2ShortcutItem("Google", "G", GoogleBlue, isDark) { onShortcutClicked("https://www.google.com") }
        O2ShortcutItem("YouTube", "YT", GoogleRed, isDark) { onShortcutClicked("https://www.youtube.com") }
        O2ShortcutItem("Wikipedia", "W", Color(0xFF546E7A), isDark) { onShortcutClicked("https://www.wikipedia.org") }
        O2ShortcutItem("Amazon", "a", GoogleYellow, isDark) { onShortcutClicked("https://www.amazon.com") }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        O2ShortcutItem("GitHub", "GH", Color(0xFF24292E), isDark) { onShortcutClicked("https://github.com") }
        O2ShortcutItem("Reddit", "R", Color(0xFFFF4500), isDark) { onShortcutClicked("https://www.reddit.com") }
        O2ShortcutItem("DuckDuckGo", "D", Color(0xFFDE5833), isDark) { onShortcutClicked("https://duckduckgo.com") }
        O2ShortcutItem("Facebook", "f", Color(0xFF1877F2), isDark) { onShortcutClicked("https://www.facebook.com") }
      }
    }

    Spacer(modifier = Modifier.height(40.dp))

    // Leadership Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onOpenAbout() },
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = omniboxBg)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text("O2 TECHNOLOGIES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
        Spacer(modifier = Modifier.height(2.dp))
        Text("ASIF ANSARI, CEO", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        Text("A biology technologist that aims to democratize technology.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = "O2 Browser Lite • Stable Version 1.0",
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    )
  }
}

@Composable
fun O2ShortcutItem(
  title: String,
  symbol: String,
  badgeColor: Color,
  isDark: Boolean,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .width(68.dp)
      .clickable(onClick = onClick),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Surface(
      modifier = Modifier.size(48.dp),
      shape = CircleShape,
      color = if (isDark) O2ShortcutBgDark else O2ShortcutBgLight
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(
          text = symbol,
          fontWeight = FontWeight.Bold,
          color = badgeColor,
          fontSize = 17.sp
        )
      }
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = title,
      fontSize = 11.sp,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}

/**
 * O2 Incognito Home Screen
 */
@Composable
fun O2IncognitoHomeScreen(
  onSearchSubmit: (String) -> Unit,
  blockThirdPartyCookies: Boolean,
  onToggleCookies: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF1F1F1F))
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 24.dp, vertical = 24.dp),
    horizontalAlignment = Alignment.Start
  ) {
    Spacer(modifier = Modifier.height(20.dp))

    Box(
      modifier = Modifier
        .size(68.dp)
        .clip(CircleShape)
        .background(Color(0xFF333333)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.VisibilityOff,
        contentDescription = null,
        tint = Color(0xFFE8EAED),
        modifier = Modifier.size(36.dp)
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = "You've gone incognito",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFFE8EAED)
    )

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "Now you can browse privately in O2 Browser, and other people who use this device won't see your activity. However, downloads and bookmarks will be saved.",
      fontSize = 14.sp,
      color = Color(0xFF9AA0A6),
      lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Incognito Omnibox
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      shape = RoundedCornerShape(24.dp),
      color = Color(0xFF2B2B2B)
    ) {
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF9AA0A6), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search or type URL", fontSize = 14.sp, color = Color(0xFF9AA0A6)) },
          singleLine = true,
          modifier = Modifier.weight(1f),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Search),
          keyboardActions = KeyboardActions(onSearch = { if (searchQuery.isNotBlank()) onSearchSubmit(searchQuery) }),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(28.dp))

    Text(text = "O2 Browser won't save:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFE8EAED))
    Spacer(modifier = Modifier.height(6.dp))
    Text("• Your browsing history\n• Cookies and site data\n• Information entered in forms", fontSize = 13.sp, color = Color(0xFF9AA0A6), lineHeight = 20.sp)

    Spacer(modifier = Modifier.height(18.dp))

    Text(text = "Your activity might still be visible to:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFE8EAED))
    Spacer(modifier = Modifier.height(6.dp))
    Text("• Websites that you visit\n• Your employer or school\n• Your internet service provider", fontSize = 13.sp, color = Color(0xFF9AA0A6), lineHeight = 20.sp)

    Spacer(modifier = Modifier.height(24.dp))
    HorizontalDivider(color = Color(0xFF333333))
    Spacer(modifier = Modifier.height(16.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text("Block third-party cookies", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFFE8EAED))
        Text("When on, sites can't use cookies that track you across the web", fontSize = 12.sp, color = Color(0xFF9AA0A6))
      }
      Switch(
        checked = blockThirdPartyCookies,
        onCheckedChange = onToggleCookies,
        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = O2BlueDark)
      )
    }

    Spacer(modifier = Modifier.height(30.dp))
  }
}
