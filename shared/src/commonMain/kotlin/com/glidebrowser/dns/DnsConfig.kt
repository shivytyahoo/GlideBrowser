package com.glidebrowser.dns

/** A set like IPChakra's 27 DNS providers — a small curated subset + Custom. */
data class DnsProvider(val id: String, val name: String, val ips: List<String>, val doh: String? = null, val blocksAds: Boolean = false)
object DnsProviders {
    val ALL = listOf(
        DnsProvider("cloudflare", "Cloudflare", listOf("1.1.1.1", "1.0.0.1"), "https://cloudflare-dns.com/dns-query"),
        DnsProvider("cloudflare-ads", "Cloudflare (Ads blocked)", listOf("1.1.1.2", "1.0.0.2"), blocksAds = true),
        DnsProvider("quad9", "Quad9", listOf("9.9.9.9", "149.112.112.112"), "https://dns.quad9.net/dns-query"),
        DnsProvider("adguard", "AdGuard DNS", listOf("94.140.14.14", "94.140.15.15"), "https://dns.adguard-dns.com/dns-query", blocksAds = true),
        DnsProvider("nextdns", "NextDNS", listOf("45.90.28.0"), "https://dns.nextdns.io", blocksAds = true),
        DnsProvider("google", "Google", listOf("8.8.8.8", "8.8.4.4"), "https://dns.google/dns-query"),
        DnsProvider("controld", "Control D", listOf("76.76.2.0"), blocksAds = true),
        DnsProvider("cleanbrowsing", "CleanBrowsing", listOf("185.228.168.9"), blocksAds = true),
        DnsProvider("mullvad", "Mullvad", listOf("194.242.2.2"), "https://dns.mullvad.net/dns-query"),
        DnsProvider("custom", "Custom", emptyList()),
    )
    fun byId(id: String) = ALL.firstOrNull { it.id == id } ?: ALL[0]
}
data class DnsSettings(val providerId: String = "cloudflare", val customIp: String = "", val useDoh: Boolean = true)
