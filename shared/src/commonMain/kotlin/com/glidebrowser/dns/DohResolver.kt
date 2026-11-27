package com.glidebrowser.dns
/** Minimal DNS-over-HTTPS contract — the implementation calls `https://<provider>/dns-query?name=<host>&type=A` via OkHttp.
 *  Only the URL is built here, so platform code can test it. */
object DohResolver {
    fun queryUrl(provider: DnsProvider, host: String): String? {
        val base = provider.doh ?: return null
        return "$base?name=$host&type=A"
    }
}
