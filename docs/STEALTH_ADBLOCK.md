# Stealth AdBlocker — website detect na kar paye

Seedha block karne se site ko pata chal jata hai: ad request fail hota hai, `div` gayab hota hai, ya bait element ka size 0 hota hai. ShadowBlock paper (WWW'19) ka idea: DOM waisa hi dikhao jaise adblock na ho, par user ko ad dikhao mat.

## GlideBrowser me 3 layers (S-AdShield se liya)
1. **DNS layer (S-AdShield):** blocked domain → local stub IP `10.10.10.3`. Wahan chhota stub server empty-but-valid jawab deta hai (1x1 gif, empty JS, empty HTML). Request fail nahi hoti, isliye `onerror` / fetch-fail wale detectors trigger nahi hote. Anti-Detect OFF ho to NXDOMAIN.
2. **Request layer (WebView):** `shouldInterceptRequest` me EasyList/`||domain` match ho to `WebResourceResponse("text/plain", "utf-8", emptyStream)` return karo — `null` mat return karo (null se fail dikhta hai). Sirf third-party ad/tracker domains, first-party content ko chhedo mat. Allowlist (`@@`) pehle check karo.
3. **Cosmetic layer (JS/CSS):** ad slots ko `display:none` karne ke bajaye unki height collapse karo aur bait elements (`#ad-test`, `ins.adsbygoogle`) ko as-it-is rehne do — sirf unke andar ka content empty rakho. `window` pe adblock-detect wale globals ko fake mat karo, wo fingerprint ban jata hai.

## Lists
StevenBlack + HaGeZi Multi PRO + uBlock DNS + ad-network pack + Streaming SSAI pack (Google DAI/IMA, FreeWheel, SpotX...). Suffix-walk matching: `doubleclick.net` block karne se `ads.doubleclick.net` bhi block. Update: `Lists -> Update now`, Wi-Fi only option.

## Honest limit
Same-host ads (content aur ad ek hi CDN se) DNS/request se alag nahi hote. Ye koi bhi adblocker nahi kar sakta bina page tode.
