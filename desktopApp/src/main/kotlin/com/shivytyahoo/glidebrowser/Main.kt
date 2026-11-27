package com.shivytyahoo.glidebrowser
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.glidebrowser.adblock.StealthAdBlocker
import com.glidebrowser.dns.DnsProviders
import com.glidebrowser.settings.BrowserSettings
/** Windows: Compose Desktop + real Edge WebView2 (system runtime, no bundled
 *  Chromium, so the installer stays small). Pages render in WebView2Panel.kt
 *  (SWT.EDGE bridged through SwingPanel + SWT_AWT); shared core gives the
 *  same Stealth AdBlock / VPN / DNS behaviour as Android. */
@OptIn(ExperimentalMaterial3Api::class)
fun main() = application {
    var settings by remember { mutableStateOf(BrowserSettings()) }
    var url by remember { mutableStateOf("https://duckduckgo.com") }
    var addressText by remember { mutableStateOf(url) }
    val controller = remember { WebView2Controller() }
    val blocker = remember { StealthAdBlocker(setOf("doubleclick.net", "googlesyndication.com", "googleadservices.com", "ads.yahoo.com")) }
    Window(onCloseRequest = ::exitApplication, title = "GlideBrowser — Windows") {
        MaterialTheme {
            Scaffold(topBar = { TopAppBar(title = { Text("GlideBrowser") }) }) { p ->
                Column(Modifier.padding(p).fillMaxSize()) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { controller.goBack() }) { Text("Back") }
                        Button(onClick = { controller.reload() }) { Text("Reload") }
                        OutlinedTextField(value = addressText, onValueChange = { addressText = it }, modifier = Modifier.weight(1f), singleLine = true)
                        Button(onClick = { controller.navigateTo(addressText) }) { Text("Go") }
                    }
                    WebView2Panel(
                        url = url,
                        controller = controller,
                        adBlockEnabled = settings.adBlockEnabled,
                        blocker = blocker,
                        onUrlChanged = { newUrl -> url = newUrl; addressText = newUrl },
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row { Text("AdBlock"); Switch(checked = settings.adBlockEnabled, onCheckedChange = { settings = settings.copy(adBlockEnabled = it) }) }
                        Row { Text("VPN — IPChakra"); Switch(checked = settings.vpnEnabled, onCheckedChange = { settings = settings.copy(vpnEnabled = it) }) }
                        Text("DNS: ${DnsProviders.byId(settings.dns.providerId).name}", style = MaterialTheme.typography.bodySmall)
                        Text("About: github.com/shivytyahoo", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
