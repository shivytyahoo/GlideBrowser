package com.glidebrowser.video
/** Download contract — Android implementation uses OkHttp + Range (resume), Desktop uses the same OkHttp.
 *  HLS: download segments in parallel, decrypt with the AES-128 key if present, then build MP4 from .ts concat.
 *  DRM (Widevine) streams are not supported. */
data class DownloadTask(val id: String, val url: String, val fileName: String, val kind: MediaKind, val totalBytes: Long? = null, val progress: Float = 0f, val state: String = "queued")
interface MediaDownloader {
    suspend fun downloadDirect(task: DownloadTask, onProgress: (Float) -> Unit): Boolean
    suspend fun downloadHls(playlistUrl: String, fileName: String, quality: HlsVariant?, onProgress: (Float) -> Unit): Boolean
}
