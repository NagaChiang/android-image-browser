package com.timespawn.androidimagebrowser

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout.HORIZONTAL
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
import com.timespawn.androidimagebrowser.models.PixabayApi
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

    private lateinit var imageRecyclerView : RecyclerView
    private lateinit var progressOverlay : View
    private lateinit var defaultLayoutMode: LayoutMode
    private lateinit var currentLayoutMode: LayoutMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        defaultLayoutMode = LayoutMode.List // TODO: Remote default value
        currentLayoutMode = getLayoutModeFromPreferences()

        imageRecyclerView = findViewById(R.id.imageRecyclerView)
        updateImageRecyclerViewLayout(currentLayoutMode)

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

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun onSearchQueryReceived(query: String) {
        progressOverlay.visibility = VISIBLE

        val newDatas = PixabayApi.searchImages(query)
        if (newDatas != null) {
            imageDatas.clear()
            imageDatas.addAll(newDatas)
            imageRecyclerView.adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "Failed to search images", Toast.LENGTH_LONG).show()
        }

        progressOverlay.visibility = GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val layoutSwitchItem = menu?.findItem(R.id.layoutSwitch)
        layoutSwitchItem?.icon = getLayoutModeIcon(getLayoutModeFromPreferences())

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.layoutSwitch -> {
                currentLayoutMode = if (currentLayoutMode == LayoutMode.List) LayoutMode.Grid else LayoutMode.List
                setLayoutModeToPreferences(currentLayoutMode)
                item.icon = getLayoutModeIcon(currentLayoutMode)
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

        Log.w(TAG, "intToLayoutMode(): Failed to convert int to LayoutMode ($value), returns default layout mode instead")

        return defaultLayoutMode
    }

    private fun updateImageRecyclerViewLayout(mode: LayoutMode) {
        when (mode) {
            LayoutMode.List -> {
                imageRecyclerView.addItemDecoration(DividerItemDecoration(this, VERTICAL))
                imageRecyclerView.adapter = linearAdapter
                imageRecyclerView.layoutManager = LinearLayoutManager(this)
            }

            LayoutMode.Grid -> {
                imageRecyclerView.removeItemDecoration(DividerItemDecoration(this, VERTICAL))
                imageRecyclerView.adapter = gridAdapter
                imageRecyclerView.layoutManager = GridLayoutManager(this, ITEM_PER_ROW)
            }
        }
    }
}