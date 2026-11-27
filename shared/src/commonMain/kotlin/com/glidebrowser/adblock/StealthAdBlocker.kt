package com.glidebrowser.adblock

/** Shared port of S-AdShield (github.com/shivytyahoo/S-AdShield).
 *  DNS + request contract for both layers. Platform code (Android WebView /
 *  Windows WebView2) implements this interface.
 *  Matching: suffix-walk — `doubleclick.net` also blocks `ads.doubleclick.net`.
 */
data class BlockDecision(val blocked: Boolean, val reason: String = "")

class StealthAdBlocker(
    blockedDomains: Set<String>,
    private val allowlist: Set<String> = emptySet(),
) {
    private val blocked = blockedDomains.map { it.trim().lowercase() }.toSet()
    fun shouldBlock(url: String): BlockDecision {
        val host = runCatching { java.net.URI(url).host?.lowercase() }.getOrNull() ?: return BlockDecision(false)
        if (allowlist.any { host == it || host.endsWith(".$it") }) return BlockDecision(false, "allowlisted")
        var cur: String? = host
        while (cur != null) {
            if (cur in blocked) return BlockDecision(true, "matched $cur")
            cur = cur.substringAfter(".", "").ifEmpty { null }
        }
        return BlockDecision(false)
    }
    companion object {
        /** Stub IP — like S-AdShield: a blocked domain gets an empty-but-valid reply. */
        const val STUB_IP = "10.10.10.3"
        val EMPTY_GIF = byteArrayOf(0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 1, 0, 1, 0, 0x80.toByte(), 0, 0, 0, 0, 0, 0, 0, 0, 0x21, 0xF9.toByte(), 4, 1, 0, 0, 0, 0, 0x2C, 0, 0, 0, 0, 1, 0, 1, 0, 0, 2, 2, 0x44, 1, 0, 0x3B)
    }
}
