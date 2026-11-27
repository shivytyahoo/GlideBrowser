# Stealth AdBlocker — So Websites Cannot Detect It

Blocking directly gives it away: the ad request fails, a `div` disappears, or a bait element becomes size 0. The ShadowBlock paper (WWW'19) idea: show the DOM as if there were no adblocker, but do not show the ad to the user.

## Three layers in GlideBrowser (taken from S-AdShield)
1. **DNS layer (S-AdShield):** blocked domain → local stub IP `10.10.10.3`. A small stub server there returns an empty-but-valid response (1x1 gif, empty JS, empty HTML). The request does not fail, so `onerror` / fetch-fail detectors are not triggered. With Anti-Detect OFF, the answer is NXDOMAIN.
2. **Request layer (WebView):** in `shouldInterceptRequest`, if an EasyList / `||domain` rule matches, return `WebResourceResponse("text/plain", "utf-8", emptyStream)` — do not return `null` (null looks like a failure). Only third-party ad / tracker domains; first-party content is left alone. Check the allowlist (`@@`) first.
3. **Cosmetic layer (JS/CSS):** instead of hiding ad slots with `display:none`, collapse their height and leave bait elements (`#ad-test`, `ins.adsbygoogle`) as they are — only keep the content inside them empty. Do not fake adblock-detect globals on `window`; that becomes a fingerprint.

## Lists
StevenBlack + HaGeZi Multi PRO + uBlock DNS + ad-network pack + Streaming SSAI pack (Google DAI/IMA, FreeWheel, SpotX...). Suffix-walk matching: blocking `doubleclick.net` also blocks `ads.doubleclick.net`. Update: `Lists -> Update now`, Wi-Fi only option.

## Honest limit
Same-host ads (content and ad from the same CDN) cannot be separated at DNS / request level. No adblocker can do that without breaking the page.
