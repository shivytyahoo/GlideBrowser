package com.glidebrowser.settings
data class TabItem(val id: String, val url: String, val title: String = "", val incognito: Boolean = false)
data class HistoryEntry(val url: String, val title: String, val visitedAt: Long)
data class Bookmark(val url: String, val title: String, val addedAt: Long)
