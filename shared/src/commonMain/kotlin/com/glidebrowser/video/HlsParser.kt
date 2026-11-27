package com.glidebrowser.video
/** HLS (.m3u8) parsing — QuickTube / super-video-downloader pattern. */
data class HlsVariant(val bandwidth: Long, val resolution: String?, val url: String)
data class HlsSegment(val url: String, val durationSec: Double)
data class HlsPlaylist(val variants: List<HlsVariant> = emptyList(), val segments: List<HlsSegment> = emptyList(), val keyUrl: String? = null, val isMaster: Boolean = false)
object HlsParser {
    fun parse(text: String, baseUrl: String): HlsPlaylist {
        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val variants = mutableListOf<HlsVariant>(); val segments = mutableListOf<HlsSegment>()
        var key: String? = null; var pendingBw = 0L; var pendingRes: String? = null; var pendingDur = 0.0
        fun resolve(u: String) = if (u.startsWith("http")) u else baseUrl.substringBeforeLast("/") + "/" + u.removePrefix("/")
        for (line in lines) {
            when {
                line.startsWith("#EXT-X-STREAM-INF") -> { pendingBw = Regex("BANDWIDTH=(\\d+)").find(line)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
                    pendingRes = Regex("RESOLUTION=([0-9x]+)").find(line)?.groupValues?.get(1) }
                line.startsWith("#EXT-X-KEY") -> key = Regex("URI=\"([^\"]+)\"").find(line)?.groupValues?.get(1)
                line.startsWith("#EXTINF") -> pendingDur = line.substringAfter(":").substringBefore(",").toDoubleOrNull() ?: 0.0
                !line.startsWith("#") -> if (pendingBw > 0) { variants.add(HlsVariant(pendingBw, pendingRes, resolve(line))); pendingBw = 0 } else segments.add(HlsSegment(resolve(line), pendingDur))
            }
        }
        return HlsPlaylist(variants, segments, key, variants.isNotEmpty())
    }
}
