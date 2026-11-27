package com.shivytyahoo.glidebrowser
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.glidebrowser.dns.DnsProviders
import com.glidebrowser.settings.BrowserSettings
/** Windows: Compose Desktop + WebView2 (Edge runtime).
 *  No bundled Chromium — WebView2 ships with Windows 10/11, so the installer stays ~20 MB.
 *  Connecting WebView2 through a JNI/COM bridge is still pending — this shell shows shared settings + tabs + media list;
 *  the engine bridge will live in `desktopApp/src/main/kotlin/.../WebView2Bridge.kt` (MS WebView2 SDK + JNA). */
@OptIn(ExperimentalMaterial3Api::class)
fun main() = application {
    var settings by remember { mutableStateOf(BrowserSettings()) }
    var url by remember { mutableStateOf("https://duckduckgo.com") }
    Window(onCloseRequest = ::exitApplication, title = "GlideBrowser — Windows") {
        MaterialTheme {
            Scaffold(topBar = { TopAppBar(title = { Text("GlideBrowser") }) }) { p ->
                Column(Modifier.padding(p).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row { OutlinedTextField(value = url, onValueChange = { url = it }, modifier = Modifier.weight(1f), singleLine = true)
                        Button(onClick = {}) { Text("Go") } }
                    Text("The WebView2 view will go here — pages will load through the Windows Edge runtime. Shared core: AdBlock / VPN / DNS / VideoSniffer, same as on Android.")
                    Text("AdBlock: ${if (settings.adBlockEnabled) "ON" else "OFF"} • VPN: ${if (settings.vpnEnabled) "ON" else "OFF"} • DNS: ${DnsProviders.byId(settings.dns.providerId).name}")
                    Row { Text("Stealth AdBlock"); Switch(checked = settings.adBlockEnabled, onCheckedChange = { settings = settings.copy(adBlockEnabled = it) }) }
                    Row { Text("VPN — IPChakra"); Switch(checked = settings.vpnEnabled, onCheckedChange = { settings = settings.copy(vpnEnabled = it) }) }
                    Text("About: github.com/shivytyahoo • S-AdShield • IPChakra", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
