package com.example.echoposts.ui.favourites

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.echoposts.R
import androidx.core.graphics.drawable.toDrawable

class SwipeToDeleteCallback(
    private val context: Context,
    private val onSwipeDelete: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    
    private val deleteIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_delete)
    private val background = Color.RED.toDrawable()
    
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
    
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwipeDelete(viewHolder.adapterPosition)
    }
    
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20
        
        val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
        val iconBottom = iconTop + deleteIcon.intrinsicHeight
        
        if (dX < 0) { // Swiping to the left
            val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
            val iconRight = itemView.right - iconMargin
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            
            background.setBounds(
                itemView.right + dX.toInt() - backgroundCornerOffset,
                itemView.top,
                itemView.right,
                itemView.bottom
            )
        } else { // View is unSwiped
            background.setBounds(0, 0, 0, 0)
        }
        
        background.draw(c)
        deleteIcon.draw(c)
        
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}