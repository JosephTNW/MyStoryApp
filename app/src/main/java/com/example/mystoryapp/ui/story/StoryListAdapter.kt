package com.example.mystoryapp.ui.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.response.GetStoryResult
import com.example.mystoryapp.databinding.StoryListBinding

class StoryListAdapter :
    ListAdapter<StoryEntity, StoryListAdapter.StoryViewHolder>(DIFFUTIL_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryEntity, view: StoryListBinding)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class StoryViewHolder(private val bind: StoryListBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun bind(getStory: StoryEntity) {
            bind.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(getStory, bind)
            }

            bind.apply {
                Glide.with(itemView)
                    .load(getStory.photoUrl)
                    .into(ivItemPhoto)
                tvItemName.text = getStory.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = StoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object {
        private val DIFFUTIL_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(
                oldItem: StoryEntity,
                newItem: StoryEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: StoryEntity,
                newItem: StoryEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}