package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.O2BlueDark
import com.example.ui.theme.O2BlueLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun O2DropdownMenu(
  expanded: Boolean,
  onDismiss: () -> Unit,
  canGoForward: Boolean,
  isBookmarked: Boolean,
  desktopMode: Boolean,
  onForward: () -> Unit,
  onBookmark: () -> Unit,
  onReload: () -> Unit,
  onNewTab: () -> Unit,
  onNewIncognitoTab: () -> Unit,
  onHistory: () -> Unit,
  onDeleteData: () -> Unit,
  onDownloads: () -> Unit,
  onBookmarks: () -> Unit,
  onToggleDesktop: () -> Unit,
  onSettings: () -> Unit,
  onAbout: () -> Unit,
  onHelp: () -> Unit,
  isDark: Boolean
) {
  DropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismiss,
    modifier = Modifier
      .width(260.dp)
      .background(if (isDark) Color(0xFF28292A) else Color(0xFFFFFFFF))
      .testTag("o2_menu")
  ) {
    // 1. Top action icons row (Forward, Bookmark, Download, Reload)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(if (isDark) Color(0xFF35363A) else Color(0xFFF1F3F4))
        .padding(horizontal = 4.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = { onForward(); onDismiss() }, enabled = canGoForward) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = stringResource(R.string.forward),
          tint = if (canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
          modifier = Modifier.size(20.dp)
        )
      }
      IconButton(onClick = { onBookmark(); onDismiss() }) {
        Icon(
          imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
          contentDescription = stringResource(R.string.bookmarks_title),
          tint = if (isBookmarked) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.size(20.dp)
        )
      }
      IconButton(onClick = { onDownloads(); onDismiss() }) {
        Icon(
          imageVector = Icons.Default.FileDownload,
          contentDescription = stringResource(R.string.downloads_title),
          tint = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.size(20.dp)
        )
      }
      IconButton(onClick = { onReload(); onDismiss() }) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = stringResource(R.string.reload),
          tint = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    HorizontalDivider(color = if (isDark) Color(0xFF3C4043) else Color(0xFFE8EAED))

    // 2. Standard O2 Browser Menu Items
    O2MenuItem(label = stringResource(R.string.new_tab), icon = Icons.Default.Add, onClick = { onNewTab(); onDismiss() })
    O2MenuItem(label = stringResource(R.string.new_incognito_tab), icon = Icons.Default.VisibilityOff, onClick = { onNewIncognitoTab(); onDismiss() })

    HorizontalDivider(color = if (isDark) Color(0xFF3C4043) else Color(0xFFE8EAED))

    O2MenuItem(label = stringResource(R.string.history_title), icon = Icons.Default.History, onClick = { onHistory(); onDismiss() })
    O2MenuItem(label = stringResource(R.string.delete_browsing_data), icon = Icons.Default.Delete, onClick = { onDeleteData(); onDismiss() })
    O2MenuItem(label = stringResource(R.string.downloads_title), icon = Icons.Default.Download, onClick = { onDownloads(); onDismiss() })
    O2MenuItem(label = stringResource(R.string.bookmarks_title), icon = Icons.Default.StarOutline, onClick = { onBookmarks(); onDismiss() })

    HorizontalDivider(color = if (isDark) Color(0xFF3C4043) else Color(0xFFE8EAED))

    // Desktop Site Checkbox
    DropdownMenuItem(
      text = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DesktopWindows, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(R.string.desktop_mode), fontSize = 14.sp)
          }
          Checkbox(
            checked = desktopMode,
            onCheckedChange = { onToggleDesktop(); onDismiss() },
            colors = CheckboxDefaults.colors(checkedColor = if (isDark) O2BlueDark else O2BlueLight)
          )
        }
      },
      onClick = { onToggleDesktop(); onDismiss() }
    )

    HorizontalDivider(color = if (isDark) Color(0xFF3C4043) else Color(0xFFE8EAED))

    O2MenuItem(label = stringResource(R.string.settings), icon = Icons.Default.Settings, onClick = { onSettings(); onDismiss() })
    O2MenuItem(label = stringResource(R.string.help_support), icon = Icons.AutoMirrored.Filled.HelpOutline, onClick = { onHelp(); onDismiss() })
    O2MenuItem(label = stringResource(R.string.about), icon = Icons.Default.Info, onClick = { onAbout(); onDismiss() })
  }
}

@Composable
fun O2MenuItem(
  label: String,
  icon: ImageVector,
  onClick: () -> Unit
) {
  DropdownMenuItem(
    text = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(label, fontSize = 14.sp)
      }
    },
    onClick = onClick
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun O2SettingsSheet(
  settings: BrowserSettings,
  onDismiss: () -> Unit,
  onThemeChange: (ThemeMode) -> Unit,
  onToggleProgressBar: (Boolean) -> Unit,
  onToggleAdBlocker: (Boolean) -> Unit,
  onToggleExcessiveAds: (Boolean) -> Unit,
  onToggleThirdPartyCookies: (Boolean) -> Unit,
  onOpenAbout: () -> Unit,
  onOpenHelp: () -> Unit,
  isDark: Boolean
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val primaryBlue = if (isDark) O2BlueDark else O2BlueLight

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = if (isDark) Color(0xFF202124) else Color(0xFFFFFFFF),
    modifier = Modifier.testTag("settings_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("O2 Browser Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Developer Card - ASIF ANSARI, CEO OF O2 TECHNOLOGIES
      Card(
        modifier = Modifier.fillMaxWidth().clickable { onOpenAbout() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF28292A) else Color(0xFFF1F3F4))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(primaryBlue),
              contentAlignment = Alignment.Center
            ) {
              Text("O₂", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(stringResource(R.string.developer_name), fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text(
                stringResource(R.string.developer_role),
                style = MaterialTheme.typography.bodySmall,
                color = primaryBlue,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            stringResource(R.string.developer_bio),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Quick Help & About Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onOpenAbout,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("About O2", fontSize = 13.sp)
        }
        Button(
          onClick = onOpenHelp,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
        ) {
          Icon(Icons.Default.Help, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Help & Support", fontSize = 13.sp)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
      HorizontalDivider(color = if (isDark) Color(0xFF3C4043) else Color(0xFFE8EAED))
      Spacer(modifier = Modifier.height(14.dp))

      // Appearance
      Text("APPEARANCE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = primaryBlue)
      Spacer(modifier = Modifier.height(8.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ThemeFilterChip("System", settings.themeMode == ThemeMode.SYSTEM, { onThemeChange(ThemeMode.SYSTEM) }, Modifier.weight(1f), primaryBlue)
        ThemeFilterChip("Light", settings.themeMode == ThemeMode.LIGHT, { onThemeChange(ThemeMode.LIGHT) }, Modifier.weight(1f), primaryBlue)
        ThemeFilterChip("Dark", settings.themeMode == ThemeMode.DARK, { onThemeChange(ThemeMode.DARK) }, Modifier.weight(1f), primaryBlue)
      }

      Spacer(modifier = Modifier.height(14.dp))

      O2SwitchRow(
        title = stringResource(R.string.settings_progress_bar_title),
        desc = stringResource(R.string.settings_progress_bar_desc),
        checked = settings.showProgressBar,
        onCheckedChange = onToggleProgressBar,
        primaryBlue = primaryBlue
      )

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = if (isDark) Color(0xFF3C4043) else Color(0xFFE8EAED))
      Spacer(modifier = Modifier.height(14.dp))

      // Privacy & Content Filtering
      Text("PRIVACY & PROTECTION", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = primaryBlue)
      Spacer(modifier = Modifier.height(8.dp))

      O2SwitchRow(
        title = stringResource(R.string.settings_ad_blocker_title),
        desc = stringResource(R.string.settings_ad_blocker_desc),
        checked = settings.adBlockerEnabled,
        onCheckedChange = onToggleAdBlocker,
        primaryBlue = primaryBlue
      )

      Spacer(modifier = Modifier.height(10.dp))

      O2SwitchRow(
        title = stringResource(R.string.settings_excessive_ads_title),
        desc = stringResource(R.string.settings_excessive_ads_desc),
        checked = settings.excessiveAdsBlockEnabled,
        onCheckedChange = onToggleExcessiveAds,
        primaryBlue = primaryBlue
      )

      Spacer(modifier = Modifier.height(10.dp))

      O2SwitchRow(
        title = stringResource(R.string.settings_cookies_title),
        desc = stringResource(R.string.settings_cookies_desc),
        checked = settings.blockThirdPartyCookies,
        onCheckedChange = onToggleThirdPartyCookies,
        primaryBlue = primaryBlue
      )

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
      ) {
        Text(stringResource(R.string.close), fontWeight = FontWeight.SemiBold)
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

/**
 * Dedicated About Section Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun O2AboutSheet(
  onDismiss: () -> Unit,
  onOpenHelp: () -> Unit,
  isDark: Boolean
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val primaryBlue = if (isDark) O2BlueDark else O2BlueLight

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = if (isDark) Color(0xFF202124) else Color(0xFFFFFFFF),
    modifier = Modifier.testTag("about_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("About O2 Browser", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Logo
      O2Logo(modifier = Modifier.size(68.dp))

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "O2 Browser Lite",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold
      )

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = primaryBlue.copy(alpha = 0.15f),
        modifier = Modifier.padding(top = 4.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Speed, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Stable Version 1.0 • 70% Faster Browsing",
            color = primaryBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF28292A) else Color(0xFFF1F3F4))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "LEADERSHIP & VISION",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = primaryBlue
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "ASIF ANSARI",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "CEO OF O2 TECHNOLOGIES",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = primaryBlue
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "A biology technologist that aims to democratize technology.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF28292A) else Color(0xFFF1F3F4))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "PERFORMANCE BENCHMARK",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = primaryBlue
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = stringResource(R.string.browser_about_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 21.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      Button(
        onClick = { onDismiss(); onOpenHelp() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
      ) {
        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Contact Support Team", fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

/**
 * Dedicated Help & Support Section Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun O2HelpSheet(
  onDismiss: () -> Unit,
  isDark: Boolean
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current
  val primaryBlue = if (isDark) O2BlueDark else O2BlueLight

  fun sendEmail() {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
      data = Uri.parse("mailto:zenith4asif@gmail.com")
      putExtra(Intent.EXTRA_SUBJECT, "O2 Browser Lite Support Request")
    }
    try {
      context.startActivity(Intent.createChooser(intent, "Send Email"))
    } catch (_: Exception) {
      val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:zenith4asif@gmail.com"))
      try { context.startActivity(fallback) } catch (_: Exception) {}
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = if (isDark) Color(0xFF202124) else Color(0xFFFFFFFF),
    modifier = Modifier.testTag("help_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Help & Support", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = primaryBlue.copy(alpha = 0.12f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Email, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Official Support Channel", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryBlue)
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "zenith4asif@gmail.com",
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Working Hours: 10:00 AM – 3:00 AM", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "We will reply in 3–5 working days.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Direct email button
      Button(
        onClick = { sendEmail() },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
      ) {
        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Email zenith4asif@gmail.com", fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedButton(
        onClick = onDismiss,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Close", fontWeight = FontWeight.SemiBold)
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

/**
 * O2 Tabs Manager Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun O2TabsManagerSheet(
  tabs: List<BrowserTab>,
  activeTabId: String,
  onSwitchTab: (String) -> Unit,
  onCloseTab: (String) -> Unit,
  onNewTab: () -> Unit,
  onNewIncognitoTab: () -> Unit,
  onDismiss: () -> Unit,
  isDark: Boolean
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val primaryBlue = if (isDark) O2BlueDark else O2BlueLight
  val surfaceBg = if (isDark) Color(0xFF202124) else Color(0xFFFFFFFF)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = surfaceBg,
    modifier = Modifier.testTag("tabs_manager_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("O2 Tabs (${tabs.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Tab Action Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { onNewTab(); onDismiss() },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(stringResource(R.string.new_tab), fontSize = 13.sp)
        }
        OutlinedButton(
          onClick = { onNewIncognitoTab(); onDismiss() },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.VisibilityOff, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(stringResource(R.string.new_incognito_tab), fontSize = 13.sp)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = if (isDark) Color(0xFF3C4043) else Color(0xFFE8EAED))
      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 420.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(tabs, key = { it.id }) { tab ->
          val isActive = tab.id == activeTabId
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onSwitchTab(tab.id); onDismiss() }
              .then(
                if (isActive) Modifier.border(2.dp, primaryBlue, RoundedCornerShape(10.dp))
                else Modifier
              ),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (tab.isIncognito) Color(0xFF2A2B2E)
              else if (isDark) Color(0xFF28292A)
              else Color(0xFFF1F3F4)
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(if (tab.isIncognito) Color(0xFF3C4043) else primaryBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (tab.isIncognito) Icons.Default.VisibilityOff else Icons.Default.Public,
                  contentDescription = null,
                  tint = if (tab.isIncognito) Color(0xFFE8EAED) else primaryBlue,
                  modifier = Modifier.size(20.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = tab.title.ifBlank { "New Tab" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                  )
                  if (isActive) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      color = primaryBlue,
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        text = "ACTIVE",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                      )
                    }
                  }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = tab.url.ifBlank { "Start Page" },
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              if (tabs.size > 1) {
                IconButton(onClick = { onCloseTab(tab.id) }) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_tab),
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

/**
 * O2 Bookmarks Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun O2BookmarksSheet(
  bookmarks: List<BookmarkItem>,
  onSelectBookmark: (String) -> Unit,
  onDeleteBookmark: (String) -> Unit,
  onDismiss: () -> Unit,
  isDark: Boolean
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val primaryBlue = if (isDark) O2BlueDark else O2BlueLight
  val surfaceBg = if (isDark) Color(0xFF202124) else Color(0xFFFFFFFF)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = surfaceBg,
    modifier = Modifier.testTag("bookmarks_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Bookmarks (${bookmarks.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (bookmarks.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.BookmarkBorder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(56.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = stringResource(R.string.no_bookmarks),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Tap the bookmark star in the menu to save websites.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 420.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(bookmarks, key = { it.id }) { item ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectBookmark(item.url) },
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF28292A) else Color(0xFFF1F3F4))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFBBF24).copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = item.title.ifBlank { item.url },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = item.url,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
                IconButton(onClick = { onDeleteBookmark(item.id) }) {
                  Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

/**
 * O2 History Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun O2HistorySheet(
  history: List<HistoryItem>,
  onSelectHistory: (String) -> Unit,
  onDeleteHistoryItem: (String) -> Unit,
  onClearAll: () -> Unit,
  onDismiss: () -> Unit,
  isDark: Boolean
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val primaryBlue = if (isDark) O2BlueDark else O2BlueLight
  val surfaceBg = if (isDark) Color(0xFF202124) else Color(0xFFFFFFFF)
  val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = surfaceBg,
    modifier = Modifier.testTag("history_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Browsing History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (history.isNotEmpty()) {
            OutlinedButton(
              onClick = onClearAll,
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Text(stringResource(R.string.clear_history), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(6.dp))
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (history.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(56.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = stringResource(R.string.no_history),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 420.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(history, key = { it.id }) { item ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectHistory(item.url) },
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF28292A) else Color(0xFFF1F3F4))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(primaryBlue.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.AccessTime, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = item.title.ifBlank { item.url },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = item.url,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = dateFormat.format(Date(item.timestamp)),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                  )
                }
                IconButton(onClick = { onDeleteHistoryItem(item.id) }) {
                  Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

/**
 * O2 Downloads Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun O2DownloadsSheet(
  downloads: List<DownloadItem>,
  onSelectDownload: (String) -> Unit,
  onClearDownloads: () -> Unit,
  onDismiss: () -> Unit,
  isDark: Boolean
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val primaryBlue = if (isDark) O2BlueDark else O2BlueLight
  val surfaceBg = if (isDark) Color(0xFF202124) else Color(0xFFFFFFFF)
  val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = surfaceBg,
    modifier = Modifier.testTag("downloads_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Downloads (${downloads.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (downloads.isNotEmpty()) {
            OutlinedButton(
              onClick = onClearDownloads,
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Text(stringResource(R.string.clear_downloads), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(6.dp))
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (downloads.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.Download,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(56.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = stringResource(R.string.no_downloads),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 420.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(downloads, key = { it.id }) { item ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectDownload(item.url) },
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF28292A) else Color(0xFFF1F3F4))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(primaryBlue.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = item.fileName.ifBlank { "downloaded_file" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = if (item.contentLength > 0) "${item.contentLength / 1024} KB • ${dateFormat.format(Date(item.timestamp))}"
                    else dateFormat.format(Date(item.timestamp)),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

/**
 * O2 Clear Data Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun O2ClearDataSheet(
  onConfirmClear: (clearHistory: Boolean, clearCookies: Boolean, clearCache: Boolean, clearTabs: Boolean) -> Unit,
  onDismiss: () -> Unit,
  isDark: Boolean
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val primaryBlue = if (isDark) O2BlueDark else O2BlueLight
  val surfaceBg = if (isDark) Color(0xFF202124) else Color(0xFFFFFFFF)

  var clearHistory by remember { mutableStateOf(true) }
  var clearCookies by remember { mutableStateOf(true) }
  var clearCache by remember { mutableStateOf(true) }
  var clearTabs by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = surfaceBg,
    modifier = Modifier.testTag("clear_data_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Delete Browsing Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      ClearDataOptionRow(
        title = "Browsing history",
        desc = "Clears all visits from history",
        checked = clearHistory,
        onCheckedChange = { clearHistory = it },
        primaryBlue = primaryBlue
      )

      ClearDataOptionRow(
        title = "Cookies and site data",
        desc = "Clears site logins and persistent session data",
        checked = clearCookies,
        onCheckedChange = { clearCookies = it },
        primaryBlue = primaryBlue
      )

      ClearDataOptionRow(
        title = "Cached images and files",
        desc = "Frees up local storage and resets downloaded web assets",
        checked = clearCache,
        onCheckedChange = { clearCache = it },
        primaryBlue = primaryBlue
      )

      ClearDataOptionRow(
        title = "Close open tabs",
        desc = "Closes all active tabs and returns to new tab",
        checked = clearTabs,
        onCheckedChange = { clearTabs = it },
        primaryBlue = primaryBlue
      )

      Spacer(modifier = Modifier.height(20.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Cancel")
        }
        Button(
          onClick = {
            onConfirmClear(clearHistory, clearCookies, clearCache, clearTabs)
          },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
        ) {
          Text("Clear Data", fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(18.dp))
    }
  }
}

@Composable
fun ClearDataOptionRow(
  title: String,
  desc: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  primaryBlue: Color
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCheckedChange(!checked) }
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Checkbox(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = CheckboxDefaults.colors(checkedColor = primaryBlue)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
      Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
fun O2SwitchRow(
  title: String,
  desc: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  primaryBlue: Color
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCheckedChange(!checked) }
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
      Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Spacer(modifier = Modifier.width(8.dp))
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = primaryBlue)
    )
  }
}

@Composable
fun ThemeFilterChip(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  primaryBlue: Color
) {
  FilterChip(
    selected = selected,
    onClick = onClick,
    label = {
      Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 12.sp)
    },
    leadingIcon = if (selected) {
      { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
    } else null,
    modifier = modifier,
    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = primaryBlue.copy(alpha = 0.2f), selectedLabelColor = primaryBlue)
  )
}

/**
 * Distinctive O2 Logo
 */
@Composable
fun O2Logo(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .clip(CircleShape)
      .background(O2BlueLight),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "O₂",
      color = Color.White,
      fontSize = 32.sp,
      fontWeight = FontWeight.Black
    )
  }
}
