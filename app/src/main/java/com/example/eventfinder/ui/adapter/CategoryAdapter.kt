package com.example.eventfinder.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventfinder.R
import com.example.eventfinder.model.Category

// CategoryAdapter.kt
class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0 // First category selected by default

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.liveShowImage)
        private val text = view.findViewById<TextView>(R.id.liveShowText)
        private val container = view.findViewById<View>(R.id.liveShowButton) // The background container

        fun bind(category: Category, isSelected: Boolean) {
            image.setImageResource(category.iconRes)
            text.text = category.name

            if (isSelected) {
                container.setBackgroundResource(R.drawable.rounded_card_background) // Yellow background
            } else {
                container.setBackgroundResource(R.drawable.rounded_card_background_black) // Black background
            }

            container.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)
                onCategoryClick(category) // Callback to ViewModel/UI
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_button, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position == selectedPosition)
    }

    override fun getItemCount() = categories.size
}
