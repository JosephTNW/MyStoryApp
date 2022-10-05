package com.example.mystoryapp.ui.detail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.mystoryapp.R
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.response.GetStoryResult
import com.example.mystoryapp.databinding.ActivityDetailBinding
import com.example.mystoryapp.ui.login.LoginActivity
import kotlinx.coroutines.runBlocking

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private fun manifestLoading(status: Boolean){
        binding.pbLoading.visibility = if (status) View.VISIBLE else View.GONE
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manifestLoading(true)
        val info = intent.getParcelableExtra<GetStoryResult>(STORY) as GetStoryResult
        manifestLoading(false)
        binding.apply {
            tvDetailDescription.text = info.description
            tvDetailName.text = info.name
            Glide.with(this@DetailActivity)
                .load(info.photoUrl)
                .centerCrop()
                .into(ivDetailPhoto)
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
                    val pref = getSharedPreferences("token_key", Context.MODE_PRIVATE)
                    pref.edit(commit = true){
                        clear()
                    }
                }
                Intent(this@DetailActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return true
    }

    companion object {
        const val STORY = "story"
    }
}