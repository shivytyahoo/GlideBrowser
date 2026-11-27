# VPN Integration — IPChakra

Source: https://github.com/shivytyahoo/IPChakra (Flutter/Dart, v1.0.0)
Engines: WARP (WireGuard) + OpenVPN + Xray Stealth (VLESS+Reality, Hysteria2, Trojan, Shadowsocks, VMess).

## Android constraint — the most important one
Android allows only **one `VpnService`** at a time. S-AdShield is a VpnService, IPChakra is one too. They cannot run separately. So in GlideBrowser:
- **When VPN is ON:** do not start a separate S-AdShield VpnService. Move blocking inside the tunnel (IPChakra's AdShield pattern):
  - V2Ray/Xray: inject the blocklist into `dns.hosts` (`"domain:<d>": "0.0.0.0"`), `dns-out` outbound + routing rules.
  - WireGuard: set the `DNS` line of the config to an ad-blocking DNS (AdGuard).
  - OpenVPN: `pull-filter ignore "dhcp-option DNS"` + `dhcp-option DNS <adblocking DNS>`.
- **When VPN is OFF:** run the S-AdShield local-only VpnService (only DNS traffic in the tunnel, other traffic outside it).

## Verified connect (IPChakra's rule — copied)
Show "Connected" only when: (1) a DNS answer arrives through the tunnel, (2) a hostname fetch returns an exit IP different from the real IP, (3) two more probes then return the same exit IP. Before that, show "Connecting…". The watchdog probes every 30s; 3 failures → "Reconnecting". Never show a false "Connected".

## Windows
Windows has no VpnService. There, run the native WireGuard / OpenVPN / Xray binaries (wg.exe / openvpn.exe / xray.exe) as sidecar processes, or use Windows built-in VPN (RAS). The desktop module has a `VpnEngine` interface — the Android implementation uses `VpnService`, the Windows implementation is process-based. Both share the contract in `shared/.../vpn/VpnManager.kt`.
