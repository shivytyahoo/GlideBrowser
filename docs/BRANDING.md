# GlideBrowser — Branding

## Slogan (final)
**Glide Fast. Browse Free.**

Runner-ups (if it ever changes):
- Surf Smooth. Stay Private.
- No Ads. No Trackers. Just Glide.
- A light browser, heavy on privacy.

## Logo
- `docs/logo.png` — G + wing + shield, indigo/violet + cyan
- Android launcher: `androidApp/src/main/res/mipmap-xxxhdpi/ic_launcher.webp` (same art, use it as the foreground for the adaptive icon)
- GitHub banner: `docs/banner.png` — wide banner, 1280x640 style, placed at the top of the README

## Colors
- Background (dark): `#12102B` — banner / splash background
- Primary violet: `#6C4CF1`
- Accent cyan: `#00E5FF`
- Text: `#E8F6FF` (on dark)

## Splash screen
`ui/SplashScreen.kt` — logo spring scale + fade, name slide-up, slogan fade, progress bar. Total 1.8s, then `BrowserApp` opens. The animation only runs while the splash is visible.
