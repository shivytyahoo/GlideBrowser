# VPN Integration — IPChakra

Source: https://github.com/shivytyahoo/IPChakra (Flutter/Dart, v1.0.0)
Engines: WARP (WireGuard) + OpenVPN + Xray Stealth (VLESS+Reality, Hysteria2, Trojan, Shadowsocks, VMess).

## Android constraint — sabse important
Android pe ek time pe sirf **ek `VpnService`** chal sakta hai. S-AdShield bhi VpnService hai, IPChakra bhi. Dono alag-alag nahi chal sakte. Isliye GlideBrowser me:
- **VPN ON ho to:** S-AdShield ka alag VpnService start mat karo. Blocking ko tunnel ke andar shift karo (IPChakra ka AdShield pattern):
  - V2Ray/Xray: blocklist ko `dns.hosts` me inject karo (`"domain:<d>": "0.0.0.0"`), `dns-out` outbound + routing rules.
  - WireGuard: config ki `DNS` line ko ad-blocking DNS (AdGuard) pe set karo.
  - OpenVPN: `pull-filter ignore "dhcp-option DNS"` + `dhcp-option DNS <adblocking DNS>`.
- **VPN OFF ho to:** S-AdShield wala local-only VpnService chalao (sirf DNS traffic tunnel me, baaki traffic bahar).

## Verified connect (IPChakra ka rule — copy kiya)
“Connected” sirf tab dikhao jab: (1) tunnel se DNS answer aaye, (2) hostname fetch se exit IP real IP se alag mile, (3) uske baad 2 aur probes same exit IP dein. Usse pehle “Connecting…”. Watchdog har 30s probe kare; 3 fail → “Reconnecting”, kabhi jhootha “Connected” nahi.

## Windows
Windows pe VpnService nahi hota. Wahan WireGuard / OpenVPN / Xray ke native binaries (wg.exe / openvpn.exe / xray.exe) ko sidecar process ki tarah chalao, ya Windows ke built-in VPN (RAS) ko use karo. Desktop module me `VpnEngine` interface hai — Android impl `VpnService` se, Windows impl process-based hai. Dono ka contract `shared/.../vpn/VpnManager.kt` me hai.
