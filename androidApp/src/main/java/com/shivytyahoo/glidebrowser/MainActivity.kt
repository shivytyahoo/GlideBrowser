package com.shivytyahoo.glidebrowser
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.shivytyahoo.glidebrowser.ui.BrowserApp
import com.shivytyahoo.glidebrowser.ui.SplashScreen
import com.shivytyahoo.glidebrowser.ui.theme.GlideBrowserTheme
class MainActivity : ComponentActivity() {
    private val vm: BrowserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GlideBrowserTheme { Surface(color = MaterialTheme.colorScheme.background) {
            var showSplash by remember { mutableStateOf(true) }
            if (showSplash) SplashScreen(onFinished = { showSplash = false }) else BrowserApp(vm)
        } } }
    }
}
