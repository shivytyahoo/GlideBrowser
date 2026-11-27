package com.glidebrowser.adblock
/** S-AdShield lists — bundled + update. The URLs are the same as in the S-AdShield README. */
object BlocklistSources {
    const val STEVEN_BLACK = "https://raw.githubusercontent.com/StevenBlack/hosts/master/hosts"
    const val HAGEZI = "https://raw.githubusercontent.com/hagezi/dns-blocklists/main/domains/multi.txt"
    const val UBLOCK = "https://raw.githubusercontent.com/uBlockOrigin/uAssets/master/filters/filters.txt"
    val ALL = listOf(STEVEN_BLACK, HAGEZI)
}
object BlocklistRepository {
    /** hosts-file line: `0.0.0.0 domain` -> domain. Suffix-dedup: if a parent is blocked, drop the child. */
    fun parseHosts(text: String): Set<String> {
        val out = mutableSetOf<String>()
        for (line in text.lines()) {
            val t = line.trim(); if (t.isEmpty() || t.startsWith("#")) continue
            val parts = t.split(Regex("\\s+")); val d = parts.lastOrNull() ?: continue
            if (d.contains(".") && !d.startsWith("#")) out.add(d.lowercase())
        }
        return dedup(out)
    }
    fun dedup(domains: Set<String>): Set<String> {
        val sorted = domains.sortedBy { it.length }
        val kept = mutableSetOf<String>()
        for (d in sorted) { var p: String? = d.substringAfter(".", ""); var covered = false
            while (p != null && p.contains(".")) { if (p in kept) { covered = true; break }; p = p.substringAfter(".", "").ifEmpty { null } }
            if (!covered) kept.add(d) }
        return kept
    }
}
