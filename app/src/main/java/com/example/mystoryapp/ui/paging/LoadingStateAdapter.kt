package com.example.mystoryapp.ui.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mystoryapp.databinding.ItemLoadingBinding

class LoadingStateAdapter(private val tryLoad: () -> Unit) : LoadStateAdapter<LoadingStateAdapter.LoadingViewHolder>() {

    class LoadingViewHolder(private val binding: ItemLoadingBinding, tryLoad: () -> Unit) : RecyclerView.ViewHolder(binding.root){

        init {
            binding.retryButton.setOnClickListener { tryLoad.invoke() }
        }

        fun bind(loadState: LoadState){
            if(loadState is LoadState.Error){
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.visibility = if (loadState is LoadState.Loading) View.VISIBLE else View.GONE
            binding.retryButton.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            binding.errorMsg.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
        }
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding, tryLoad)
    }

}