package com.shivytyahoo.glidebrowser.ui
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shivytyahoo.glidebrowser.R
import kotlinx.coroutines.delay
/** Stylish animated splash — M3 motion only, no gradient / glass.
 *  Sequence (total ~1.8s, phir browser khulta hai):
 *  1. Logo scale 0.7 -> 1.0 + fade in (spring)
 *  2. "GlideBrowser" letters fade/slide up
 *  3. Slogan "Glide Fast. Browse Free." fade in
 *  4. Progress bar 0 -> 100% (WebView warm-up ke saath)
 *  Animation band ho jati hai jab splash hat jata hai — hidden layer me kuch animate nahi hota. */
@Composable fun SplashScreen(onFinished: () -> Unit) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true; delay(1800); onFinished() }
    val logoScale by animateFloatAsState(if (started) 1f else 0.7f, animationSpec = spring(dampingRatio = 0.6f, stiffness = 180f), label = "logoScale")
    val logoAlpha by animateFloatAsState(if (started) 1f else 0f, animationSpec = tween(500), label = "logoAlpha")
    val textAlpha by animateFloatAsState(if (started) 1f else 0f, animationSpec = tween(600, delayMillis = 350), label = "textAlpha")
    val sloganAlpha by animateFloatAsState(if (started) 1f else 0f, animationSpec = tween(600, delayMillis = 700), label = "sloganAlpha")
    val progress by animateFloatAsState(if (started) 1f else 0f, animationSpec = tween(1700, easing = FastOutSlowInEasing), label = "progress")
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Image(painter = painterResource(R.drawable.ic_glide_logo), contentDescription = "GlideBrowser",
                modifier = Modifier.size(128.dp).scale(logoScale).alpha(logoAlpha), contentScale = ContentScale.Fit)
            Text("GlideBrowser", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.alpha(textAlpha))
            Text(stringResource(R.string.slogan), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.alpha(sloganAlpha))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.width(180.dp))
            Text("AdBlock • VPN • Video Downloader", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.alpha(sloganAlpha))
        }
    }
}
