package com.example.mystoryapp.ui.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.response.GetStoryResult
import com.example.mystoryapp.databinding.ActivityDetailBinding
import com.example.mystoryapp.utils.Constants.DETAIL_STORY

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private fun manifestLoading(status: Boolean) {
        binding.pbLoading.visibility = if (status) View.VISIBLE else View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manifestLoading(true)
        val info = intent.getParcelableExtra<StoryEntity>(DETAIL_STORY) as StoryEntity
        manifestLoading(false)
        binding.apply {
            tvDetailDescription.text = info.desc
            tvDetailName.text = info.name
            Glide.with(this@DetailActivity)
                .load(info.photoUrl)
                .into(ivDetailPhoto)
        }
        hideSystemUI()
    }

    private fun hideSystemUI() {
        supportActionBar?.hide()
    }
}