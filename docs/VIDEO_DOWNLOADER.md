# Video Downloader — Aloha / Soul / Via / Pawxy kaise karte hain

Ye chaaro apps closed-source hain, isliye code nahi dekha ja sakta. Unka behaviour + open-source equivalents (QuickTube, super-video-downloader, HaizhuBrowser, Pull) se ye pattern nikla hai:

## 1. Detection — 3 jagah se pakdo
1. **`WebViewClient.shouldInterceptRequest`** — har request ka URL + Content-Type dekho. `.mp4/.webm/.mkv/.m3u8/.mpd/.mp3/.m4a` ya `video/*` mile to candidate banao. Yehi Soul/Via ka main tareeka hai.
2. **JS bridge / DOM scan** — page load ke baad JS chalao: `document.querySelectorAll('video, source')` + `video.currentSrc`, iframe aur shadow-DOM ke andar bhi. Aloha ka long-press aur media toolbar isi se bharte hain.
3. **ServiceWorker requests** — kuch players (HLS) service worker se fetch karte hain, wo `shouldInterceptRequest` me nahi aate. Isliye `ServiceWorkerClient.shouldInterceptRequest` bhi lagao (super-video-downloader ka GlobalVideoDetectionModel).

Dedup karo: ek hi URL baar-baar na dikhe. Size / duration ke liye HEAD request se `Content-Length` + `Content-Type` verify karo — `.zip` jaisa dikhne wala link asal me landing page bhi ho sakta hai (Pull ka smart capture).

## 2. UI — Aloha jaisa
- Page pe video mile to bottom me **“Download video” chip / floating button** (Aloha: long-press video → Download, ya playing video pe toolbar icon).
- Tap karne pe **quality picker**: m3u8 master playlist parse karo → 144p/720p/1080p/audio-only list dikhao. Direct mp4 ho to direct download.
- Downloads screen me progress, pause/resume, speed. `DownloadManager` + foreground service (Android 14+ pe `specialUse`).

## 3. Download engines — alag-alag format, alag engine
- **Direct file (.mp4/.webm):** OkHttp, Range header se segmented + resume.
- **HLS (.m3u8):** playlist fetch karo → variant choose karo → `.ts` segments parallel download karo → AES-128 key ho to decrypt karo → MP4 me stitch karo (ffmpeg bina bhi `.ts` concat se kaam chal sakta hai, QuickTube pattern).
- **DASH (.mpd):** manifest parse karo → video+audio adaptation sets alag download karo → mux karo.
- **Live stream:** segments aate rahenge — user Stop dabaye tab tak record karo.
- **yt-dlp / NewPipe:** sirf un sites ke liye jahan direct URL nahi milta. Ye heavy hai — optional module me rakho, APK size na badhao.

## 4. Kya nahi karte
- DRM (Widevine) wali streams, login ke peeche ki paid content, ya site ke ToS ke khilaf download ko bypass nahi karte.
- Cookies: site ki cookies (WebView CookieManager) ko download request me forward karo, warna 403 aayega — par cookies ko log/store mat karo.
