package com.example.eventfinder.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventfinder.R
import com.example.eventfinder.databinding.ItemEventBinding
import com.example.eventfinder.model.Event

class MyFavoriteEventAdapter(private val eventList: List<Event>,private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<MyFavoriteEventAdapter.EventViewHolder>(){
    inner class EventViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyFavoriteEventAdapter.EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return eventList.size

    }

    override fun onBindViewHolder(holder: MyFavoriteEventAdapter.EventViewHolder, position: Int) {
        val event = eventList[position]
        val binding = holder.binding
        val imageUrl = event.images.firstOrNull()?.url
        if (imageUrl != null) {
            Glide.with(binding.ivEventImage.context)
                .load(imageUrl)
                .placeholder(R.drawable.concert) // while loading
                .into(binding.ivEventImage)
        } else {
            binding.ivEventImage.setImageResource(R.drawable.concert) // fallback
        }
        binding.tvEventName.text=event.name
        binding.tvCategory.text = "Category: ${event.classifications?.firstOrNull()?.segment?.name ?: "Unknown"}"
        binding.btnFavorite.setOnClickListener {
            onDeleteClick(event.id) // Pass event_id to callback
        }
    }

}