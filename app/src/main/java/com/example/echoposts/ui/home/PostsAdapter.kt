package com.example.echoposts.ui.home


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.echoposts.R
import com.example.echoposts.databinding.ItemLoadingBinding
import com.example.echoposts.databinding.ItemPostBinding
import com.example.echoposts.domain.model.Post

class PostsAdapter(
    private val onFavouriteClick: (Post) -> Unit,
    private val onPostClick: (Post) -> Unit = {}
) : ListAdapter<PostsAdapter.UiModel, RecyclerView.ViewHolder>(PostDiffCallback()) {

    companion object {
        private const val TYPE_POST = 0
        private const val TYPE_LOADING = 1
    }

    private var isLoadingMore = false

    sealed class UiModel {
        data class PostItem(val post: Post) : UiModel()
        object Loading : UiModel()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.PostItem -> TYPE_POST
            is UiModel.Loading -> TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_POST -> {
                val binding = ItemPostBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PostViewHolder(binding, onFavouriteClick, onPostClick)
            }
            TYPE_LOADING -> {
                val binding = ItemLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LoadingViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> {
                val postItem = getItem(position) as UiModel.PostItem
                holder.bind(postItem.post)
            }
            is LoadingViewHolder -> {
                // Loading view doesn't need binding
            }
        }
    }

    fun submitPostsWithLoading(posts: List<Post>, showLoading: Boolean) {
        val uiModels = posts.map<Post, UiModel> { UiModel.PostItem(it) }.toMutableList()
        if (showLoading) {
            uiModels.add(UiModel.Loading)
        }
        submitList(uiModels)
        isLoadingMore = showLoading
    }

    class PostViewHolder(
        private val binding: ItemPostBinding,
        private val onFavouriteClick: (Post) -> Unit,
        private val onPostClick: (Post) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                tvTitle.text = post.title
                tvBody.text = post.body
                tvUserId.text = "User ${post.userId}"

                // Set user initial in avatar
                tvUserInitial.text = tvUserId.text.subSequence(0,1)

                // Set favorite icon
                btnFavourites.setImageResource(
                    if (post.isFavourite) {
                        R.drawable.ic_favorite_filled
                    } else {
                        R.drawable.ic_favorite_border
                    }
                )

                btnFavourite.setOnClickListener {
                    onFavouriteClick(post)
                }

                root.setOnClickListener {
                    onPostClick(post)
                }
            }
        }
    }

    class LoadingViewHolder(
        binding: ItemLoadingBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private class PostDiffCallback : DiffUtil.ItemCallback<UiModel>() {
        override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
            return when {
                oldItem is UiModel.PostItem && newItem is UiModel.PostItem ->
                    oldItem.post.id == newItem.post.id
                oldItem is UiModel.Loading && newItem is UiModel.Loading -> true
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
            return oldItem == newItem
        }
    }
}