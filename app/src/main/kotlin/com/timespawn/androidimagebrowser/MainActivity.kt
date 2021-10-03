package com.timespawn.androidimagebrowser

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.SearchView

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        findViewById<SearchView>(R.id.searchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isIconifiedByDefault = false
            isSubmitButtonEnabled = true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.action == Intent.ACTION_SEARCH) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query -> onSearchQueryReceived(query) }
        }
    }

    private fun onSearchQueryReceived(query: String) {
        Log.d(TAG, "onSearchQueryReceived ($query)")

        searchImage(query)
    }

    private fun searchImage(query: String) {
        Log.d(TAG, "searchImage ($query)")
    }
}