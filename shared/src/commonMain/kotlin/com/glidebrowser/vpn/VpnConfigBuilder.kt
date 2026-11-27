package com.glidebrowser.vpn
/** IPChakra's AdShield-in-tunnel pattern — config generation, no secret hardcoded here. */
object VpnConfigBuilder {
    /** WireGuard: DNS line ko ad-blocking DNS pe badlo. */
    fun wireGuardWithAdBlocking(config: String, adDns: String = "94.140.14.14"): String {
        if (!config.contains("DNS =")) return config + "\nDNS = $adDns\n"
        return config.replace(Regex("DNS =.*"), "DNS = $adDns")
    }
    /** OpenVPN: ignore the server's DNS push, use our own ad-blocking DNS. */
    fun openVpnWithAdBlocking(config: String, adDns: String = "94.140.14.14"): String =
        config + "\npull-filter ignore \"dhcp-option DNS\"\ndhcp-option DNS $adDns\n"
    /** Xray/V2Ray: inject the blocklist into dns.hosts — `"domain:<d>": "0.0.0.0"`. The full 250k list is not inlined here; the caller supplies it from a bundled asset. */
    fun xrayHostsJson(blockedDomains: Set<String>): String {
        val entries = blockedDomains.joinToString(",") { "\"domain:$it\": \"0.0.0.0\"" }
        return "{$entries}"
    }
}
