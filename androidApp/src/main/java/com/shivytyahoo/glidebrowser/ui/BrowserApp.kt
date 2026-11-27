package com.shivytyahoo.glidebrowser.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glidebrowser.dns.DnsProviders
import com.glidebrowser.settings.BrowserSettings
import com.shivytyahoo.glidebrowser.BrowserViewModel
import com.shivytyahoo.glidebrowser.GlideWebView
import com.shivytyahoo.glidebrowser.R
import com.glidebrowser.adblock.StealthAdBlocker
// M3 tokens only — no hardcoded colors, no gradients, no glass.
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun BrowserApp(vm: BrowserViewModel) {
    val tabs by vm.tabs.collectAsState(); val active by vm.activeTab.collectAsState()
    val media by vm.media.collectAsState(); val settings by vm.settings.collectAsState()
    val blocked by vm.blockedCount.collectAsState()
    var urlText by remember { mutableStateOf(tabs.getOrNull(active)?.url ?: "https://duckduckgo.com") }
    var screen by remember { mutableStateOf("browser") } // browser | settings | downloads | history
    var showMedia by remember { mutableStateOf(false) }
    val blocker = remember { StealthAdBlocker(setOf("doubleclick.net", "googlesyndication.com", "googleadservices.com", "ads.yahoo.com")) }
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) },
            actions = {
                IconButton(onClick = { vm.newTab() }) { Icon(Icons.Default.Add, "New tab") }
                IconButton(onClick = { screen = "history" }) { Icon(Icons.Default.History, "History") }
                IconButton(onClick = { screen = "downloads" }) { Icon(Icons.Default.Download, "Downloads") }
                IconButton(onClick = { screen = "settings" }) { Icon(Icons.Default.Settings, stringResource(R.string.settings)) }
            }) },
        bottomBar = {
            if (screen == "browser") BottomAppBar {
                IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, "Back") }
                OutlinedTextField(value = urlText, onValueChange = { urlText = it }, modifier = Modifier.weight(1f), singleLine = true,
                    placeholder = { Text("Search or type URL") })
                IconButton(onClick = { vm.navigate(urlText) }) { Icon(Icons.Default.Search, "Go") }
                if (media.isNotEmpty()) { BadgedBox(badge = { Badge { Text("${media.size}") } }) {
                    IconButton(onClick = { showMedia = true }) { Icon(Icons.Default.PlayArrow, "Media") } } }
                IconButton(onClick = { vm.toggleBookmark(urlText, urlText) }) { Icon(Icons.Default.Bookmark, "Bookmark") }
            }
        }
    ) { p ->
        Box(Modifier.padding(p).fillMaxSize()) {
            when (screen) {
                "browser" -> { val tab = tabs.getOrNull(active); if (tab != null) GlideWebView(tab.url, blocker, vm, Modifier.fillMaxSize()) }
                "settings" -> SettingsScreen(settings, { vm.updateSettings(it) }, onAbout = { screen = "about" }, onBack = { screen = "browser" })
                "downloads" -> DownloadsScreen(media, onBack = { screen = "browser" })
                "history" -> HistoryScreen(vm, onBack = { screen = "browser" })
                "about" -> AboutScreen(onBack = { screen = "settings" })
            }
            if (blocked > 0) Text("Blocked: $blocked ads/trackers", modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.labelSmall)
        }
    }
    if (showMedia) MediaSheet(media, onClose = { showMedia = false })
}

@Composable fun MediaSheet(media: List<com.glidebrowser.video.DetectedMedia>, onClose: () -> Unit) {
    AlertDialog(onDismissRequest = onClose, confirmButton = { TextButton(onClick = onClose) { Text("Close") } },
        title = { Text("Videos found (${media.size})") },
        text = { LazyColumn { items(media) { m -> ListItem(headlineContent = { Text(m.url.take(80)) }, supportingContent = { Text(m.kind.name) }) } } })
}

@Composable fun DownloadsScreen(media: List<com.glidebrowser.video.DetectedMedia>, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Downloads", style = MaterialTheme.typography.headlineSmall)
        TextButton(onClick = onBack) { Text("Back") }
        if (media.isEmpty()) Text("No media detected yet. Kisi video wali site kholo — chip dikhega.")
        LazyColumn { items(media) { m -> ListItem(headlineContent = { Text(m.fileName()) }, supportingContent = { Text(m.kind.name + " • " + m.url.take(60)) },
            trailingContent = { TextButton(onClick = {}) { Text("Download") } }) } }
    }
}
private fun com.glidebrowser.video.DetectedMedia.fileName() = url.substringAfterLast("/").substringBefore("?").ifEmpty { "video.mp4" }

@Composable fun HistoryScreen(vm: BrowserViewModel, onBack: () -> Unit) {
    val h by vm.history.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("History", style = MaterialTheme.typography.headlineSmall)
        Row { TextButton(onClick = onBack) { Text("Back") }; TextButton(onClick = { vm.clearHistory() }) { Text("Clear all") } }
        LazyColumn { items(h) { e -> ListItem(headlineContent = { Text(e.url) }, supportingContent = { Text(e.title) }) } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun SettingsScreen(s: BrowserSettings, onChange: (BrowserSettings) -> Unit, onAbout: () -> Unit, onBack: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Settings", style = MaterialTheme.typography.headlineSmall); TextButton(onClick = onBack) { Text("Back to browser") } }
        item { Text("AdBlock — S-AdShield", style = MaterialTheme.typography.titleMedium)
            Row { Text("Stealth AdBlock"); Switch(checked = s.adBlockEnabled, onCheckedChange = { onChange(s.copy(adBlockEnabled = it)) }) }
            Row { Text("Anti-Detect (stub reply)"); Switch(checked = s.antiDetect, onCheckedChange = { onChange(s.copy(antiDetect = it)) }) }
            Text("Blocked: DNS + request + cosmetic. Sites cannot tell that blocking is on.", style = MaterialTheme.typography.bodySmall) }
        item { Text("VPN — IPChakra", style = MaterialTheme.typography.titleMedium)
            Row { Text("VPN"); Switch(checked = s.vpnEnabled, onCheckedChange = { onChange(s.copy(vpnEnabled = it)) }) }
            Text("WARP • OpenVPN • Xray Stealth. Shows Connected only after a verified connect.", style = MaterialTheme.typography.bodySmall) }
        item { Text("DNS", style = MaterialTheme.typography.titleMedium)
            DnsProviders.ALL.forEach { p -> Row { RadioButton(selected = s.dns.providerId == p.id, onClick = { onChange(s.copy(dns = s.dns.copy(providerId = p.id))) }); Text(p.name + if (p.blocksAds) " (ads blocked)" else "") } } }
        item { Text("Appearance", style = MaterialTheme.typography.titleMedium)
            Row { Text("Dark theme"); Switch(checked = s.darkTheme, onCheckedChange = { onChange(s.copy(darkTheme = it)) }) }
            Row { Text("Bottom toolbar"); Switch(checked = s.bottomToolbar, onCheckedChange = { onChange(s.copy(bottomToolbar = it)) }) } }
        item { Text("Search", style = MaterialTheme.typography.titleMedium); Text("Engine: ${s.searchEngine} • Homepage: ${s.homepage}") }
        item { Button(onClick = onAbout) { Text("About GlideBrowser") } }
    }
}

@Composable fun AboutScreen(onBack: () -> Unit) {
    val uri = LocalUriHandler.current
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("About", style = MaterialTheme.typography.headlineSmall)
        Text("GlideBrowser 1.0.0\nby Shiv")
        TextButton(onClick = { uri.openUri("https://github.com/shivytyahoo") }) { Text("GitHub: github.com/shivytyahoo") }
        TextButton(onClick = { uri.openUri("https://github.com/shivytyahoo/S-AdShield") }) { Text("S-AdShield — AdBlocker") }
        TextButton(onClick = { uri.openUri("https://github.com/shivytyahoo/IPChakra") }) { Text("IPChakra — VPN") }
        Text("Open-source bases: FOSS Browser, Lightning, Privacy Browser, QuickTube, super-video-downloader, HaizhuBrowser. Aloha / Soul / Via / Pawxy are closed-source — their behaviour was replicated, no code was copied.")
        TextButton(onClick = onBack) { Text("Back") }
    }
}
