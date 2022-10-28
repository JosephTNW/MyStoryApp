package com.example.mystoryapp.ui.story

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.R
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.databinding.ActivityStoryBinding
import com.example.mystoryapp.databinding.StoryListBinding
import com.example.mystoryapp.ui.detail.DetailActivity
import com.example.mystoryapp.ui.login.LoginActivity
import com.example.mystoryapp.ui.map.MapsActivity
import com.example.mystoryapp.ui.paging.LoadingStateAdapter
import com.example.mystoryapp.ui.upload.UploadActivity
import com.example.mystoryapp.utils.Constants
import com.example.mystoryapp.utils.ViewModelFactory
import kotlin.system.exitProcess

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

        adapter = StoryListAdapter()

        storyViewModel.getStoryList().observe(this) { result ->
            adapter.submitData(lifecycle, result)

        }

        binding.rvListStory.apply {
            layoutManager = LinearLayoutManager(this@StoryActivity)
            setHasFixedSize(true)
            adapter = this@StoryActivity.adapter.withLoadStateFooter(
                footer = LoadingStateAdapter{
                    this@StoryActivity.adapter.retry()
                }
            )

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
        binding.btnAddStory.setOnClickListener {
            Intent(this@StoryActivity, UploadActivity::class.java).also {
                postStoryReload.launch(it)
            }
        }
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