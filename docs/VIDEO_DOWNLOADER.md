# Video Downloader — How Aloha / Soul / Via / Pawxy Do It

These four apps are closed-source, so their code cannot be inspected. Their behaviour, combined with open-source equivalents (QuickTube, super-video-downloader, HaizhuBrowser, Pull), gives this pattern:

## 1. Detection — from three places
1. **`WebViewClient.shouldInterceptRequest`** — inspect every request URL + Content-Type. If `.mp4/.webm/.mkv/.m3u8/.mpd/.mp3/.m4a` or `video/*` is found, create a candidate. This is the main method used by Soul / Via.
2. **JS bridge / DOM scan** — after the page loads, run JS: `document.querySelectorAll('video, source')` + `video.currentSrc`, including inside iframes and shadow DOM. Aloha's long-press menu and media toolbar are filled from this.
3. **ServiceWorker requests** — some players (HLS) fetch through a service worker, which does not appear in `shouldInterceptRequest`. So also use `ServiceWorkerClient.shouldInterceptRequest` (super-video-downloader's GlobalVideoDetectionModel).

Deduplicate: do not show the same URL twice. For size / duration, verify `Content-Length` + `Content-Type` with a HEAD request — a link that looks like a `.zip` can actually be a landing page (Pull's smart capture).

## 2. UI — like Aloha
- When a video is found on the page, show a **"Download video" chip / floating button** at the bottom (Aloha: long-press video → Download, or a toolbar icon on the playing video).
- On tap, show a **quality picker**: parse the m3u8 master playlist → list 144p / 720p / 1080p / audio-only. For a direct mp4, download directly.
- Downloads screen shows progress, pause / resume, speed. `DownloadManager` + foreground service (`specialUse` on Android 14+).

## 3. Download engines — a different engine per format
- **Direct file (.mp4/.webm):** OkHttp, segmented download + resume via the Range header.
- **HLS (.m3u8):** fetch the playlist → choose a variant → download `.ts` segments in parallel → decrypt with the AES-128 key if present → stitch into MP4 (plain `.ts` concat also works without ffmpeg, QuickTube pattern).
- **DASH (.mpd):** parse the manifest → download video + audio adaptation sets separately → mux them.
- **Live stream:** segments keep arriving — record until the user taps Stop.
- **yt-dlp / NewPipe:** only for sites where no direct URL is available. This is heavy — keep it in an optional module, do not grow the APK size.

## 4. What we do not do
- We do not bypass DRM (Widevine) streams, paid content behind a login, or downloads against a site's ToS.
- Cookies: forward the site's cookies (WebView CookieManager) with the download request, otherwise a 403 follows — but never log or store cookies.
