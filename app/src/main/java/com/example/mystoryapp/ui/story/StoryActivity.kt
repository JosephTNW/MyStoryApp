package com.example.mystoryapp.ui.story

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.R
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.response.GetStoryResult
import com.example.mystoryapp.databinding.ActivityStoryBinding
import com.example.mystoryapp.ui.ViewModelFactory
import com.example.mystoryapp.ui.detail.DetailActivity
import com.example.mystoryapp.ui.login.LoginActivity
import com.example.mystoryapp.ui.login.LoginViewModel
import com.example.mystoryapp.ui.upload.UploadActivity
import com.example.mystoryapp.ui.widget.StoryRemoteViewsFactory
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var adapter: StoryListAdapter
    private lateinit var message: String
    private var error by Delegates.notNull<Boolean>()
    private val storyViewModel: StoryViewModel by viewModels()

    private fun manifestLoading(status: Boolean){
        binding.pbLoading.visibility = if (status) View.VISIBLE else View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyViewModel = ViewModelProvider(this, ViewModelFactory(application, this))[StoryViewModel::class.java]

        adapter = StoryListAdapter()
        manifestLoading(true)
        storyViewModel.getResult().observe(this@StoryActivity) {
            if (it != null) {
                message = it.message
                error = it.error
                Toast.makeText(this@StoryActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        val token = storyViewModel.getSavedToken()
        Toast.makeText(this@StoryActivity, token, Toast.LENGTH_SHORT).show()
        storyViewModel.getStoryList(this).observe(this) {
            if (it != null && !error && message == "success") {
                storyViewModel.resetLocalStory()
                adapter.submitList(it)
                var i = 0
                while (i < 5){
                    storyViewModel.addLocalStory(it[i].photoUrl, it[i].name, it[i].description)
                    i++
                }
            }
        }
        manifestLoading(false)

        adapter.setOnItemClickCallback(object : StoryListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: GetStoryResult) {
                Intent(this@StoryActivity, DetailActivity::class.java).also{
                    val getStory = GetStoryResult(
                        data.id,
                        data.name,
                        data.description,
                        data.photoUrl,
                        data.createdAt,
                        data.lat,
                        data.lon
                    )
                    it.putExtra(DetailActivity.STORY, getStory)
                    startActivity(it)
                }
            }
        })

        binding.apply {
            rvListStory.layoutManager = LinearLayoutManager(this@StoryActivity)
            rvListStory.setHasFixedSize(true)
            rvListStory.adapter = adapter
            btnAddStory.setOnClickListener {
                Intent(this@StoryActivity, UploadActivity::class.java). also {
                    startActivity(it)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                runBlocking {
                    storyViewModel.clearPrefs()
                }
                Intent(this@StoryActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return true
    }
}