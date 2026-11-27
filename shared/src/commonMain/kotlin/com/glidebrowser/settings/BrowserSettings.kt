package com.glidebrowser.settings
import com.glidebrowser.dns.DnsSettings
/** Settings saved in DataStore — a single source of truth. */
data class BrowserSettings(
    val adBlockEnabled: Boolean = true,
    val antiDetect: Boolean = true,
    val vpnEnabled: Boolean = false,
    val dns: DnsSettings = DnsSettings(),
    val searchEngine: String = "DuckDuckGo",
    val homepage: String = "https://duckduckgo.com",
    val darkTheme: Boolean = true,
    val dynamicColor: Boolean = true,
    val bottomToolbar: Boolean = true,
    val desktopMode: Boolean = false,
    val javaScriptEnabled: Boolean = true,
)
