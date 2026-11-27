package com.shivytyahoo.glidebrowser.ui.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
// M3 tokens only — no hardcoded colors, no gradients, no glass.
@Composable fun GlideBrowserTheme(dark: Boolean = true, content: @Composable () -> Unit) {
    val scheme = if (dark) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = scheme, content = content)
}
