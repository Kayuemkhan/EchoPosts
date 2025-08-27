package com.example.echoposts.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.echoposts.databinding.*
import com.example.echoposts.domain.model.SettingsItem

class SettingsAdapter(
    private val onSwitchChanged: (String, Boolean) -> Unit,
    private val onItemClick: (String) -> Unit
) : ListAdapter<SettingsItem, RecyclerView.ViewHolder>(SettingsDiffCallback()) {
    
    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_SWITCH = 1
        private const val TYPE_ACTION = 2
        private const val TYPE_INFO = 3
        private const val TYPE_DANGER = 4
    }
    
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SettingsItem.Header -> TYPE_HEADER
            is SettingsItem.SwitchItem -> TYPE_SWITCH
            is SettingsItem.ActionItem -> TYPE_ACTION
            is SettingsItem.InfoItem -> TYPE_INFO
            is SettingsItem.DangerItem -> TYPE_DANGER
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(
                ItemSettingsHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            TYPE_SWITCH -> SwitchViewHolder(
                ItemSettingsSwitchBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onSwitchChanged
            )
            TYPE_ACTION -> ActionViewHolder(
                ItemSettingsActionBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onItemClick
            )
            TYPE_INFO -> InfoViewHolder(
                ItemSettingsInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            TYPE_DANGER -> DangerViewHolder(
                ItemSettingsDangerBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onItemClick
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is SettingsItem.Header -> (holder as HeaderViewHolder).bind(item)
            is SettingsItem.SwitchItem -> (holder as SwitchViewHolder).bind(item)
            is SettingsItem.ActionItem -> (holder as ActionViewHolder).bind(item)
            is SettingsItem.InfoItem -> (holder as InfoViewHolder).bind(item)
            is SettingsItem.DangerItem -> (holder as DangerViewHolder).bind(item)
        }
    }
    
    class HeaderViewHolder(private val binding: ItemSettingsHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SettingsItem.Header) {
            binding.tvHeader.setText(item.title)
        }
    }
    
    class SwitchViewHolder(
        private val binding: ItemSettingsSwitchBinding,
        private val onSwitchChanged: (String, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: SettingsItem.SwitchItem) {
            binding.apply {
                ivIcon.setImageResource(item.icon)
                tvTitle.setText(item.title)
                item.subtitle?.let { tvSubtitle.setText(it) }

                switchSettings.setOnCheckedChangeListener(null)
                switchSettings.isChecked = item.isChecked
                switchSettings.setOnCheckedChangeListener { _, isChecked ->
                    onSwitchChanged(item.key, isChecked)
                }
                
                root.setOnClickListener {
                    switchSettings.toggle()
                }
            }
        }
    }
    
    class ActionViewHolder(
        private val binding: ItemSettingsActionBinding,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: SettingsItem.ActionItem) {
            binding.apply {
                ivIcon.setImageResource(item.icon)
                tvTitle.setText(item.title)
                item.subtitle?.let { tvSubtitle.setText(it) }
                
                ivArrow.visibility = if (item.showArrow) android.view.View.VISIBLE else android.view.View.GONE
                
                root.setOnClickListener {
                    onItemClick(item.key)
                }
            }
        }
    }
    
    class InfoViewHolder(private val binding: ItemSettingsInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SettingsItem.InfoItem) {
            binding.apply {
                ivIcon.setImageResource(item.icon)
                tvTitle.setText(item.title)
                tvValue.text = item.value
            }
        }
    }
    
    class DangerViewHolder(
        private val binding: ItemSettingsDangerBinding,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: SettingsItem.DangerItem) {
            binding.apply {
                ivIcon.setImageResource(item.icon)
                tvTitle.setText(item.title)
                item.subtitle?.let { tvSubtitle.setText(it) }
                
                root.setOnClickListener {
                    onItemClick(item.key)
                }
            }
        }
    }
    
    private class SettingsDiffCallback : DiffUtil.ItemCallback<SettingsItem>() {
        override fun areItemsTheSame(oldItem: SettingsItem, newItem: SettingsItem): Boolean {
            return when {
                oldItem is SettingsItem.Header && newItem is SettingsItem.Header -> 
                    oldItem.title == newItem.title
                oldItem is SettingsItem.SwitchItem && newItem is SettingsItem.SwitchItem -> 
                    oldItem.key == newItem.key
                oldItem is SettingsItem.ActionItem && newItem is SettingsItem.ActionItem -> 
                    oldItem.key == newItem.key
                oldItem is SettingsItem.InfoItem && newItem is SettingsItem.InfoItem -> 
                    oldItem.title == newItem.title
                oldItem is SettingsItem.DangerItem && newItem is SettingsItem.DangerItem -> 
                    oldItem.key == newItem.key
                else -> false
            }
        }
        
        override fun areContentsTheSame(oldItem: SettingsItem, newItem: SettingsItem): Boolean {
            return oldItem == newItem
        }
    }
}