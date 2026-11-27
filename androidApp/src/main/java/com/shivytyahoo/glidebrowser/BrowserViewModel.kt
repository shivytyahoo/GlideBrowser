package com.shivytyahoo.glidebrowser
import androidx.lifecycle.ViewModel
import com.glidebrowser.settings.Bookmark
import com.glidebrowser.settings.BrowserSettings
import com.glidebrowser.settings.HistoryEntry
import com.glidebrowser.settings.TabItem
import com.glidebrowser.video.DetectedMedia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
/** One ViewModel per feature — the single source for tabs/history/bookmarks. It does not run network initialisation. */
class BrowserViewModel : ViewModel() {
    private val _tabs = MutableStateFlow(listOf(TabItem(UUID.randomUUID().toString(), "https://duckduckgo.com", "Home")))
    val tabs: StateFlow<List<TabItem>> = _tabs.asStateFlow()
    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()
    private val _history = MutableStateFlow<List<HistoryEntry>>(emptyList())
    val history: StateFlow<List<HistoryEntry>> = _history.asStateFlow()
    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()
    private val _media = MutableStateFlow<List<DetectedMedia>>(emptyList())
    val media: StateFlow<List<DetectedMedia>> = _media.asStateFlow()
    private val _settings = MutableStateFlow(BrowserSettings())
    val settings: StateFlow<BrowserSettings> = _settings.asStateFlow()
    private val _blockedCount = MutableStateFlow(0)
    val blockedCount: StateFlow<Int> = _blockedCount.asStateFlow()

    fun navigate(url: String) {
        val fixed = if (url.startsWith("http")) url else "https://$url"
        _tabs.update { list -> list.mapIndexed { i, t -> if (i == _activeTab.value) t.copy(url = fixed) else t } }
        _history.update { listOf(HistoryEntry(fixed, fixed, System.currentTimeMillis())) + it.take(499) }
        _media.value = emptyList()
    }
    fun newTab(incognito: Boolean = false) {
        _tabs.update { it + TabItem(UUID.randomUUID().toString(), "https://duckduckgo.com", "New tab", incognito) }
        _activeTab.value = _tabs.value.lastIndex
    }
    fun closeTab(i: Int) { if (_tabs.value.size > 1) { _tabs.update { it.filterIndexed { idx, _ -> idx != i } }; _activeTab.value = 0 } }
    fun selectTab(i: Int) { _activeTab.value = i }
    fun addMedia(m: DetectedMedia) { if (_media.value.none { it.url == m.url }) _media.update { it + m } }
    fun onBlocked() { _blockedCount.update { it + 1 } }
    fun toggleBookmark(url: String, title: String) {
        _bookmarks.update { if (it.any { b -> b.url == url }) it.filter { b -> b.url != url } else it + Bookmark(url, title, System.currentTimeMillis()) }
    }
    fun updateSettings(s: BrowserSettings) { _settings.value = s }
    fun clearHistory() { _history.value = emptyList() }
}
