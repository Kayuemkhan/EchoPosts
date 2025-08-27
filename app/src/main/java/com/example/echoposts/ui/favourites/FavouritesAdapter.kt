package com.example.echoposts.ui.favourites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.echoposts.databinding.ItemFavouritePostBinding
import com.example.echoposts.domain.model.Post

class FavouritesAdapter(
    private val onRemoveClick: (Post) -> Unit,
    private val onPostClick: (Post) -> Unit = {}
) : ListAdapter<Post, FavouritesAdapter.FavouriteViewHolder>(FavouriteDiffCallback()) {
    
    private var deletingPostIds: Set<Int> = emptySet()
    
    fun setDeletingPosts(deletingIds: Set<Int>) {
        val oldDeletingIds = deletingPostIds
        deletingPostIds = deletingIds
        
        val changedIds = (oldDeletingIds + deletingIds) - (oldDeletingIds intersect deletingIds)
        changedIds.forEach { postId ->
            val position = currentList.indexOfFirst { it.id == postId }
            if (position != -1) {
                notifyItemChanged(position)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val binding = ItemFavouritePostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavouriteViewHolder(binding, onRemoveClick, onPostClick)
    }
    
    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val post = getItem(position)
        val isDeleting = deletingPostIds.contains(post.id)
        holder.bind(post, isDeleting)
    }
    
    class FavouriteViewHolder(
        private val binding: ItemFavouritePostBinding,
        private val onRemoveClick: (Post) -> Unit,
        private val onPostClick: (Post) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(post: Post, isDeleting: Boolean) {
            binding.apply {
                tvTitle.text = post.title
                tvBody.text = post.body
                tvUserId.text = "User ${post.userId}"
                
                tvUserInitial.text = post.userId.toString()
                
                if (isDeleting) {
                    btnRemove.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    root.alpha = 0.6f
                } else {
                    btnRemove.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    root.alpha = 1.0f
                }
                
                btnRemove.setOnClickListener {
                    if (!isDeleting) {
                        onRemoveClick(post)
                    }
                }
                
                root.setOnClickListener {
                    if (!isDeleting) {
                        onPostClick(post)
                    }
                }
            }
        }
    }
    
    private class FavouriteDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
