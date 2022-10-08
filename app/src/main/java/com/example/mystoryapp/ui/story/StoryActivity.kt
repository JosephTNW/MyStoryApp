package com.example.mystoryapp.ui.story

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.R
import com.example.mystoryapp.data.response.GetStoryResult
import com.example.mystoryapp.databinding.ActivityStoryBinding
import com.example.mystoryapp.databinding.StoryListBinding
import com.example.mystoryapp.ui.detail.DetailActivity
import com.example.mystoryapp.ui.login.LoginActivity
import com.example.mystoryapp.ui.upload.UploadActivity
import com.example.mystoryapp.utils.Constants
import com.example.mystoryapp.utils.ViewModelFactory

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var adapter: StoryListAdapter
    private val storyViewModel: StoryViewModel by viewModels()

    private fun manifestLoading(status: Boolean) {
        binding.pbLoading.visibility = if (status) View.VISIBLE else View.GONE
    }

    private val postStoryReload =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            getStories()
        }

    private fun getStories() {
        manifestLoading(true)

        val storyViewModel =
            ViewModelProvider(this, ViewModelFactory(application, this))[StoryViewModel::class.java]

        storyViewModel.getStoryList(this)

        adapter = StoryListAdapter()
        storyViewModel.getResult().observe(this@StoryActivity) {
            if (it != null) {
                Toast.makeText(this@StoryActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        storyViewModel.getStoryResult().observe(this) {
            if (it != null) {
                storyViewModel.resetLocalStory()
                adapter.submitList(it)
                storyViewModel.addLocalStory(it)
            }
        }
        manifestLoading(false)

        binding.apply {
            rvListStory.layoutManager = LinearLayoutManager(this@StoryActivity)
            rvListStory.setHasFixedSize(true)
            rvListStory.adapter = adapter
            btnAddStory.setOnClickListener {
                Intent(this@StoryActivity, UploadActivity::class.java).also {
                    postStoryReload.launch(it)
                }
            }
        }

        adapter.setOnItemClickCallback(object : StoryListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: GetStoryResult, view: StoryListBinding) {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@StoryActivity,
                        Pair(view.ivItemPhoto, "image"),
                        Pair(view.tvItemName, "name"),
                    )
                Intent(this@StoryActivity, DetailActivity::class.java).run {
                    putExtra(Constants.DETAIL_STORY, data)
                    startActivity(this, optionsCompat.toBundle())
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getStories()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                storyViewModel.clearPrefs()
                storyViewModel.resetLocalStory()
                Intent(this@StoryActivity, LoginActivity::class.java).also {
                    startActivity(it)
                    finishAffinity()
                }
            }
            R.id.menu_language -> {
                val lIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(lIntent)
            }
        }
        return true
    }
}