package com.timespawn.androidimagebrowser

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout.VERTICAL
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timespawn.androidimagebrowser.models.ImageData
import com.timespawn.androidimagebrowser.models.ImageSearchApi
import com.timespawn.androidimagebrowser.models.PixabayApi
import com.timespawn.androidimagebrowser.models.RemoteConfig
import com.timespawn.androidimagebrowser.providers.ImageSearchSuggestionProvider
import com.timespawn.androidimagebrowser.views.ImageRecyclerViewGridAdapter
import com.timespawn.androidimagebrowser.views.ImageRecyclerViewLinearAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    enum class LayoutMode {
        List,
        Grid,
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val LAYOUT_MODE_PREF_KEY = "layoutMode"
        private const val ITEM_PER_ROW = 3
    }

    private val imageDatas = arrayListOf<ImageData>()
    private val linearAdapter = ImageRecyclerViewLinearAdapter(imageDatas)
    private val gridAdapter = ImageRecyclerViewGridAdapter(imageDatas)
    private val imageSearchApi: ImageSearchApi = PixabayApi()

    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var progressOverlay: View
    private lateinit var layoutSwitchMenuItem: MenuItem

    private var defaultLayoutMode: LayoutMode = LayoutMode.List
    private var currentLayoutMode: LayoutMode = LayoutMode.List

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        imageRecyclerView = findViewById<RecyclerView>(R.id.imageRecyclerView).apply {
            adapter = linearAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        progressOverlay = findViewById(R.id.progressOverlay)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        findViewById<SearchView>(R.id.searchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isIconifiedByDefault = false
            isSubmitButtonEnabled = true
        }

        lifecycleScope.launch {
            setProgressOverlayEnabled(true)
            initialize()
            setProgressOverlayEnabled(false)
        }
    }

    private suspend fun initialize() {
        val config = RemoteConfig.fromUrl()
        if (config != null) {
            defaultLayoutMode = intToLayoutMode(config.defaultLayoutMode)
        }

        currentLayoutMode = getLayoutModeFromPreferences()
        updateImageRecyclerViewLayout(currentLayoutMode)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.action == Intent.ACTION_SEARCH) {
            intent.getStringExtra(SearchManager.QUERY)?.also {
                SearchRecentSuggestions(this, ImageSearchSuggestionProvider.AUTHORITY, ImageSearchSuggestionProvider.MODE)
                    .saveRecentQuery(it, null)

                lifecycleScope.launch {
                    setProgressOverlayEnabled(true)
                    onSearchQueryReceived(it)
                    setProgressOverlayEnabled(false)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun onSearchQueryReceived(query: String) {
        val newDatas = imageSearchApi.searchImages(query)
        if (newDatas != null) {
            imageDatas.clear()
            imageDatas.addAll(newDatas)
            imageRecyclerView.adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "Failed to search images", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        layoutSwitchMenuItem = menu?.findItem(R.id.layoutSwitch)!!
        layoutSwitchMenuItem.icon = getLayoutModeIcon(currentLayoutMode)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.layoutSwitch -> {
                currentLayoutMode = if (currentLayoutMode == LayoutMode.List) LayoutMode.Grid else LayoutMode.List
                setLayoutModeToPreferences(currentLayoutMode)
                updateImageRecyclerViewLayout(currentLayoutMode)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getLayoutModeIcon(mode: LayoutMode): Drawable {
        return when (mode) {
            LayoutMode.List -> ContextCompat.getDrawable(this, R.drawable.ic_baseline_view_list_24)!!
            LayoutMode.Grid -> ContextCompat.getDrawable(this, R.drawable.ic_baseline_grid_on_24)!!
        }
    }

    private fun getLayoutModeFromPreferences(): LayoutMode {
        val pref = getPreferences(Context.MODE_PRIVATE)
        val modeValue = pref.getInt(LAYOUT_MODE_PREF_KEY, defaultLayoutMode.ordinal)

        return intToLayoutMode(modeValue)
    }

    private fun setLayoutModeToPreferences(mode: LayoutMode) {
        val pref = getPreferences(Context.MODE_PRIVATE)
        with (pref.edit()) {
            putInt(LAYOUT_MODE_PREF_KEY, mode.ordinal)
            apply()
        }
    }

    private fun intToLayoutMode(value: Int): LayoutMode {
        LayoutMode.values().forEach {
            if (value == it.ordinal) {
                return it
            }
        }

        Log.e(TAG, "intToLayoutMode(): Failed to convert int to LayoutMode ($value), returns default layout mode instead")

        return defaultLayoutMode
    }

    private fun updateImageRecyclerViewLayout(mode: LayoutMode) {
        while (imageRecyclerView.itemDecorationCount > 0) {
            imageRecyclerView.removeItemDecorationAt(0)
        }

        when (mode) {
            LayoutMode.List -> {
                imageRecyclerView.addItemDecoration(DividerItemDecoration(this, VERTICAL))
                imageRecyclerView.adapter = linearAdapter
                imageRecyclerView.layoutManager = LinearLayoutManager(this)
            }

            LayoutMode.Grid -> {
                imageRecyclerView.adapter = gridAdapter
                imageRecyclerView.layoutManager = GridLayoutManager(this, ITEM_PER_ROW)
            }
        }

        layoutSwitchMenuItem.icon = getLayoutModeIcon(mode)
    }

    private fun setProgressOverlayEnabled(enabled: Boolean) {
        if (enabled) {
            progressOverlay.visibility = VISIBLE
        } else {
            progressOverlay.visibility = GONE
        }
    }
}