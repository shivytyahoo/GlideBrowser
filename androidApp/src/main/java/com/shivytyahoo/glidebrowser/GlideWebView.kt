package com.shivytyahoo.glidebrowser
import android.annotation.SuppressLint
import android.webkit.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.glidebrowser.adblock.StealthAdBlocker
import com.glidebrowser.video.VideoSniffer
/** System WebView — no bundled Chromium, which is why the APK stays small. */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GlideWebView(url: String, blocker: StealthAdBlocker, vm: BrowserViewModel, modifier: Modifier = Modifier) {
    AndroidView(modifier = modifier, factory = { ctx ->
        WebView(ctx).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                    val u = request.url.toString()
                    val kind = VideoSniffer.classify(u, null)
                    if (kind != null) vm.addMedia(com.glidebrowser.video.DetectedMedia(u, kind))
                    if (blocker.shouldBlock(u).blocked) { vm.onBlocked()
                        return WebResourceResponse("text/plain", "utf-8", java.io.ByteArrayInputStream(ByteArray(0))) }
                    return super.shouldInterceptRequest(view, request)
                }
                override fun onPageFinished(view: WebView, url: String) {
                    // DOM scan: <video>/<source> + shadow-DOM players (the Aloha-style media toolbar is fed by this)
                    view.evaluateJavascript("(function(){var s=[];document.querySelectorAll('video,source').forEach(function(e){if(e.src||e.currentSrc)s.push(e.src||e.currentSrc)});return JSON.stringify(s)})();") { r ->
                        r?.trim('"')?.split(",")?.forEach { src -> val u = src.trim().trim('"','[',']'); if (u.startsWith("http")) vm.addMedia(com.glidebrowser.video.DetectedMedia(u, com.glidebrowser.video.MediaKind.DIRECT_MP4)) }
                    }
                }
            }
            webChromeClient = WebChromeClient()
            setDownloadListener { dUrl, _, _, mime, _ ->
                val kind = VideoSniffer.classify(dUrl, mime) ?: com.glidebrowser.video.MediaKind.DIRECT_MP4
                vm.addMedia(com.glidebrowser.video.DetectedMedia(dUrl, kind))
            }
            loadUrl(url)
        }
    }, update = { if (it.url != url) it.loadUrl(url) })
}
