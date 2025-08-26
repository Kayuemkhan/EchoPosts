package com.example.echoposts.ui.common

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addPaginationScrollListener(
    threshold: Int = 2,
    onLoadMore: () -> Unit
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            
            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            
            // Check if we should load more
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - threshold
                && firstVisibleItemPosition >= 0
                && totalItemCount > 0) {
                onLoadMore()
            }
        }
    })
}

fun RecyclerView.isNearBottom(threshold: Int = 2): Boolean {
    val layoutManager = this.layoutManager as? LinearLayoutManager ?: return false
    val visibleItemCount = layoutManager.childCount
    val totalItemCount = layoutManager.itemCount
    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
    
    return visibleItemCount + firstVisibleItemPosition >= totalItemCount - threshold
}