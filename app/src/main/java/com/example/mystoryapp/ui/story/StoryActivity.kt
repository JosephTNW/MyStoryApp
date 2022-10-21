package com.example.mystoryapp.ui.story

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.R
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.response.GetStoryResult
import com.example.mystoryapp.databinding.ActivityStoryBinding
import com.example.mystoryapp.databinding.StoryListBinding
import com.example.mystoryapp.ui.detail.DetailActivity
import com.example.mystoryapp.ui.login.LoginActivity
import com.example.mystoryapp.ui.upload.UploadActivity
import com.example.mystoryapp.utils.Constants
import com.example.mystoryapp.utils.ViewModelFactory
import kotlin.system.exitProcess
import com.example.mystoryapp.data.repository.Result
import com.example.mystoryapp.ui.map.MapsActivity
import com.example.mystoryapp.ui.upload.UploadViewModel

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var adapter: StoryListAdapter
    private val storyViewModel: StoryViewModel by viewModels()

    private val postStoryReload =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            getStories()
        }

    private fun getStories() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val storyViewModel : StoryViewModel by viewModels{
            factory
        }
        storyViewModel.getStoryList()

        adapter = StoryListAdapter()
        storyViewModel.getResult().observe(this@StoryActivity) {
            if (it != null) {
                Toast.makeText(this@StoryActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        storyViewModel.getStoryList().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.pbLoading.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.pbLoading.visibility = View.GONE
                        val storyList = result.data
                        adapter.submitList(storyList)
                    }
                    is Result.Error -> {
                        binding.pbLoading.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Terjadi Kesalahan" + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

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
            override fun onItemClicked(data: StoryEntity, view: StoryListBinding) {
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

    override fun onResume() {
        super.onResume()
        getStories()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        alert.setTitle(getString(R.string.exit))
            .setMessage(getString(R.string.exit_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                moveTaskToBack(true)
                android.os.Process.killProcess(android.os.Process.myPid())
                exitProcess(1)
            }
            .setNegativeButton(getString(R.string.no)) { dialog : DialogInterface, _: Int ->
                dialog.cancel()
            }
        alert.create().show()
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
            R.id.menu_map -> {
                Intent(this@StoryActivity, MapsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return true
    }
}