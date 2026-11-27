package com.shivytyahoo.glidebrowser
import com.glidebrowser.video.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
/** Segmented + resume download (Range header). HLS: segments download karke concat karo. */
class OkHttpDownloader(private val client: OkHttpClient = OkHttpClient()) : MediaDownloader {
    override suspend fun downloadDirect(task: DownloadTask, onProgress: (Float) -> Unit): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val file = File("/sdcard/Download/GlideBrowser", task.fileName).apply { parentFile?.mkdirs() }
            val req = Request.Builder().url(task.url).header("Range", "bytes=${if (file.exists()) file.length() else 0}-").build()
            client.newCall(req).execute().use { resp ->
                val total = resp.body?.contentLength() ?: -1L
                resp.body?.byteStream()?.use { input -> file.outputStream().use { out -> val buf = ByteArray(8192); var n: Int; var done = 0L
                    while (input.read(buf).also { n = it } != -1) { out.write(buf, 0, n); done += n; if (total > 0) onProgress(done.toFloat() / total) } } }
            }
            true
        }.getOrDefault(false)
    }
    override suspend fun downloadHls(playlistUrl: String, fileName: String, quality: HlsVariant?, onProgress: (Float) -> Unit): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val text = client.newCall(Request.Builder().url(playlistUrl).build()).execute().body?.string() ?: return@runCatching false
            val pl = HlsParser.parse(text, playlistUrl)
            val variant = quality ?: pl.variants.maxByOrNull { it.bandwidth }
            val mediaUrl = variant?.url ?: playlistUrl
            val mediaText = if (variant != null) client.newCall(Request.Builder().url(mediaUrl).build()).execute().body?.string() ?: return@runCatching false else text
            val media = HlsParser.parse(mediaText, mediaUrl)
            val out = File("/sdcard/Download/GlideBrowser", fileName).apply { parentFile?.mkdirs() }
            out.outputStream().use { o -> media.segments.forEachIndexed { i, s ->
                client.newCall(Request.Builder().url(s.url).build()).execute().body?.bytes()?.let { o.write(it) }
                onProgress((i + 1).toFloat() / media.segments.size) } }
            true
        }.getOrDefault(false)
    }
}
