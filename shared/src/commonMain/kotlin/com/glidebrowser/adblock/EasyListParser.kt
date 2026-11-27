package com.glidebrowser.adblock
/** Privacy Browser (Stoutner)-style EasyList parsing — `||`, `@@`, `$domain=`, `$third-party`, `*` wildcard.
 *  Only the options WebView exposes are handled; the rest are ignored, never breaking a block. */
data class FilterRule(val pattern: String, val isAllow: Boolean, val domains: Set<String> = emptySet(), val thirdPartyOnly: Boolean = false)
object EasyListParser {
    fun parse(lines: List<String>): List<FilterRule> {
        val out = mutableListOf<FilterRule>()
        for (raw in lines) {
            val line = raw.trim()
            if (line.isEmpty() || line.startsWith("!") || line.startsWith("[")) continue
            if (line.startsWith("##") || line.contains("##") || line.contains("#@#")) continue // cosmetic — alag layer me
            val allow = line.startsWith("@@")
            val body = if (allow) line.removePrefix("@@") else line
            val parts = body.split("$", limit = 2)
            val pat = parts[0]
            var domains = emptySet<String>(); var third = false
            if (parts.size > 1) for (opt in parts[1].split(",")) {
                when { opt.startsWith("domain=") -> domains = opt.removePrefix("domain=").split("|").toSet()
                       opt == "third-party" -> third = true }
            }
            if (pat.isNotEmpty()) out.add(FilterRule(pat, allow, domains, third))
        }
        return out
    }
    /** `||example.com/banner` = starts with the domain or a subdomain; `*` = wildcard segment. */
    fun matches(rule: FilterRule, url: String, pageHost: String?): Boolean {
        val host = extractHost(url)
        if (rule.domains.isNotEmpty() && pageHost != null && pageHost !in rule.domains) return false
        if (rule.thirdPartyOnly && pageHost != null && (host == pageHost || host.endsWith(".$pageHost"))) return false
        var pat = rule.pattern
        if (pat.startsWith("||")) { val d = pat.removePrefix("||").substringBefore("/"); if (!(host == d || host.endsWith(".$d"))) return false; pat = pat.substringAfter(d, "") }
        if (pat.startsWith("|")) pat = pat.removePrefix("|")
        if (pat.isEmpty()) return true
        val segs = pat.split("*").filter { it.isNotEmpty() }
        var idx = 0
        for (s in segs) { val i = url.indexOf(s, idx); if (i < 0) return false; idx = i + s.length }
        return true
    }
}
