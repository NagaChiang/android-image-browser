package com.timespawn.androidimagebrowser.providers

import android.content.SearchRecentSuggestionsProvider

class ImageSearchSuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.timespawn.androidimagebrowser.ImageSearchSuggestionProvider"
        const val MODE = DATABASE_MODE_QUERIES
    }
}