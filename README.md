# GlideBrowser

![GlideBrowser banner](docs/banner.png)

> **Glide Fast. Browse Free.** — a small, fast, private browser. No ads, no trackers, just glide.

A small, fast, private web browser for **Android and Windows PC**, built from scratch in Kotlin.

- **Android:** Kotlin + Jetpack Compose + **Material 3** (stable M3 tokens only — no gradients, no glassmorphism, no hardcoded `Color(0xFF...)`)
- **Windows:** Compose Multiplatform (Desktop) + **WebView2** (Edge runtime, already installed on Windows 10/11 — so the installer stays small, no bundled Chromium)
- **Engine:** system WebView on Android, WebView2 on Windows. No custom Chromium fork — that is why the APK stays small (Cromite / Firefox forks are 150 MB+).

## Build status (2026-11-27)

Work started — the full app code is written: shared core (EasyList, HLS, DNS, VPN config), Android WebView + tabs / history / bookmarks / downloads + Settings / About, Windows Compose Desktop shell. **This machine has no Java / Android SDK, so no APK was compiled here** — GitHub Actions (`.github/workflows/build.yml`) builds the Android APK and the Windows MSI. That is the reliable route, the same pattern used for YouGlideTube.

## Features

| Feature | How it works |
|---|---|
| **Stealth AdBlocker** | A port of `S-AdShield` — DNS + request level. 250k+ domains (StevenBlack, HaGeZi Multi PRO, uBlock DNS). A blocked domain gets an empty-but-valid answer from the `10.10.10.3` stub, so a site cannot detect the adblocker (Anti-Detect). Empty response in `shouldInterceptRequest`, cosmetic CSS collapses empty ad slots, bait elements are left untouched. |
| **Built-in VPN — IPChakra** | WARP (WireGuard) + OpenVPN + Xray Stealth (VLESS + Reality, Hysteria2, Trojan, Shadowsocks). Verified connect only — "Connected" is shown only after the DNS + exit-IP probes pass. 30s watchdog, self-healing. Android allows only one `VpnService` at a time, so AdBlock + VPN share one tunnel: V2Ray uses `dns.hosts -> 0.0.0.0`, WireGuard / OpenVPN use an ad-blocking DNS. |
| **Video Downloader** | Like Aloha / Soul / Via / Pawxy — `.mp4/.webm/.m3u8/.mpd/.mp3` sniffing via `shouldInterceptRequest` + JS bridge, a floating "N media" chip, a quality picker, and HLS segments downloaded and stitched into MP4. DRM / protected YouTube streams are **not** bypassed — only content a site serves openly. |
| **DNS change** | 27 providers (Cloudflare 1.1.1.1, Quad9, AdGuard, NextDNS, Control D, CleanBrowsing…), DoH / DoT + Custom DNS. The DNS actually in use is shown on the Home card. |
| **Tabs, History, Bookmarks, Downloads, Incognito, Reader mode, Dark / Light + Dynamic color** | DataStore + Room, Material 3 |

## GitHub projects used as reference (not copy-pasted)

Aloha, Soul Browser, Via Browser and Pawxy are **closed-source** — their code is not on GitHub, so nothing was copied from them. Their *behaviour* was studied from open-source projects and implemented from scratch:

- **FOSS Browser** (`saschpe/FOSS-Browser`) + **Lightning Browser** (`anthonycr/Lightning-Browser`) — lightweight WebView browser architecture, small-APK reference
- **Privacy Browser** (`stoutner/PrivacyBrowserAndroid`) — EasyList parsing (`||domain`, `@@allow`, `domain=` rules)
- **Cromite** (`uazo/cromite`) + **DuckDuckGo Android** (`duckduckgo/Android`) — ad / tracker blocking reference, **not forked** (size reasons)
- **QuickTube** (`devgalib/QuickTube`) — Compose M3 + NewPipe Extractor + yt-dlp, HLS-to-MP4, WebView video detection
- **super-video-downloader** (`alexch33/super-video-downloader`) — `shouldInterceptRequest` + ServiceWorker m3u8 / mpd detection, segmented downloader
- **HaizhuBrowser** (`HaizhuAI/HaizhuBrowser`) — resource sniffer + m3u8 downloader, Android + Windows
- **Pull** (`aiyu-ayaan/Pull`) — "N media" sniffer chip + 3-layer shield pattern
- **Candy Browser** (`sk2andy/candy-browser`) + **Eddy** (`arcbaseproject/Eddy`) — Compose M3 Expressive browser UI reference

My own projects:
- **S-AdShield:** https://github.com/shivytyahoo/S-AdShield — DNS adblocker, Kotlin, v1.2.0
- **IPChakra:** https://github.com/shivytyahoo/IPChakra — VPN, Dart/Flutter, WARP + Xray
- **GitHub:** https://github.com/shivytyahoo

> How the Aloha / Soul / Via style video downloader works is analysed in full in `docs/VIDEO_DOWNLOADER.md`. Stealth adblock is analysed in `docs/STEALTH_ADBLOCK.md`.

## Size — how it stays small

- System WebView / WebView2 — no bundled Chromium (~100 MB saved)
- R8 full mode + `shrinkResources`, ABI split (`arm64-v8a` separate, `armeabi-v7a` separate), `resConfigs en,hi`
- No heavy libraries: OkHttp + DataStore + Room only. ExoPlayer / Media3 only for the built-in player, in an optional module
- Target: **Android APK ~9–12 MB** (arm64, release, R8), **Windows installer ~18–25 MB** (WebView2 runtime separate, already installed)

## Build

```bash
# Android (JDK 17, Android SDK 34+)
./gradlew :androidApp:assembleRelease
# -> androidApp/build/outputs/apk/release/

# Windows (JDK 17)
./gradlew :desktopApp:packageMsi   # or packageExe
# -> desktopApp/build/compose/binaries/main/msi/
```

> No `java` on this machine yet, so no APK was built here — the commands above run in Android Studio / GitHub Actions. The GitHub Actions workflow is in `.github/workflows/build.yml` (same pattern as YouGlideTube).

## Structure

```
shared/          commonMain — AdBlock, DNS, VPN contract, VideoSniffer, Settings (platform-agnostic)
androidApp/      Compose M3 UI + WebView + VpnService (S-AdShield) + DownloadManager
desktopApp/      Compose Desktop UI + WebView2 bridge (Windows)
docs/            VIDEO_DOWNLOADER.md, STEALTH_ADBLOCK.md, VPN_INTEGRATION.md
```

## License / Note

Only download videos you have the right to download, or that a site allows. No DRM bypass.
