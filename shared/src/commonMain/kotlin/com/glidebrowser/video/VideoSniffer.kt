package com.glidebrowser.video

/** Aloha/Soul/Via jaisa detection — open-source pattern (QuickTube / super-video-downloader / HaizhuBrowser).
 *  Candidates arrive from both shouldInterceptRequest and the JS bridge; dedup + classify happens here.
 */
enum class MediaKind { DIRECT_MP4, HLS_M3U8, DASH_MPD, AUDIO, UNKNOWN }
data class DetectedMedia(val url: String, val kind: MediaKind, val title: String? = null, val sizeBytes: Long? = null, val qualities: List<String> = emptyList())
object VideoSniffer {
    private val VIDEO_EXT = listOf(".mp4", ".webm", ".mkv", ".mov", ".m3u8", ".mpd", ".mp3", ".m4a", ".ogg")
    fun classify(url: String, contentType: String?): MediaKind? {
        val u = url.lowercase()
        return when {
            ".m3u8" in u -> MediaKind.HLS_M3U8
            ".mpd" in u -> MediaKind.DASH_MPD
            contentType?.startsWith("video/") == true -> MediaKind.DIRECT_MP4
            contentType?.startsWith("audio/") == true -> MediaKind.AUDIO
            VIDEO_EXT.any { it in u } -> MediaKind.DIRECT_MP4
            else -> null
        }
    }
    fun isCandidate(url: String, contentType: String?): Boolean = classify(url, contentType) != null
}
