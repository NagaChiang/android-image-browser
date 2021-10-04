package com.timespawn.androidimagebrowser

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timespawn.androidimagebrowser.models.ImageData
import com.timespawn.androidimagebrowser.views.ImageRecyclerViewAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var imageRecyclerView : RecyclerView
    private lateinit var progressOverlay : View

    private var imageDatas = arrayListOf<ImageData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        imageRecyclerView = findViewById<RecyclerView>(R.id.imageRecyclerView).apply {
            adapter = ImageRecyclerViewAdapter(imageDatas)
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        progressOverlay = findViewById(R.id.progressOverlay)

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
            intent.getStringExtra(SearchManager.QUERY)?.also { query -> lifecycleScope.launch { onSearchQueryReceived(query) } }
        }
    }

    private suspend fun onSearchQueryReceived(query: String) {
        progressOverlay.visibility = VISIBLE

        imageDatas.clear()
        imageDatas.addAll(searchImage(query))
        imageRecyclerView.adapter?.notifyDataSetChanged()

        progressOverlay.visibility = GONE
    }

    private suspend fun searchImage(query: String): ArrayList<ImageData> {
        // TODO
        Log.d(TAG, "Search images with query '$query'")
        delay(1000)

        return arrayListOf(
            ImageData("adfsaf"),
            ImageData("ddd"),
            ImageData("fdsf"),
            ImageData("ewe"),
            ImageData("gegdsdf"),
            ImageData("fe3ffdsv"),
            ImageData("vrbrb"),
            ImageData("23r32"),
            ImageData("efeffe"),
        )
    }
}