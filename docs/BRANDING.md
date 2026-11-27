# GlideBrowser — Branding

## Slogan (final)
**Glide Fast. Browse Free.**

Runner-ups (agar kabhi badalna ho):
- Surf Smooth. Stay Private.
- No Ads. No Trackers. Just Glide.
- Halka browser, bhaari privacy. (Hinglish)

## Logo
- `docs/logo.png` — G + wing + shield, indigo/violet + cyan
- Android launcher: `androidApp/src/main/res/mipmap-xxxhdpi/ic_launcher.webp` (same art, adaptive icon ke liye foreground me use karo)
- GitHub banner: `docs/banner.png` — 1280x640 jaisa wide banner, README ke top pe laga hai

## Colors
- Background (dark): `#12102B` — banner / splash background
- Primary violet: `#6C4CF1`
- Accent cyan: `#00E5FF`
- Text: `#E8F6FF` (on dark)

## Splash screen
`ui/SplashScreen.kt` — logo spring scale + fade, naam slide-up, slogan fade, progress bar. Total 1.8s, phir `BrowserApp` khulta hai. Animation sirf splash dikhte waqt chalti hai.
