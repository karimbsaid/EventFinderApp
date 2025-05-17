package com.example.eventfinder.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventfinder.R
import com.example.eventfinder.model.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.liveShowText)
        private val container = view.findViewById<View>(R.id.liveShowButton)

        fun bind(category: Category, isSelected: Boolean) {
            text.text = category.name

            if (isSelected) {
                container.setBackgroundResource(R.drawable.rounded_card_background_selected)
                text.setTextColor(container.context.getColor(R.color.white))
            } else {
                container.setBackgroundResource(R.drawable.rounded_card_background)
                text.setTextColor(container.context.getColor(R.color.black))
            }

            container.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)
                onCategoryClick(category)
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
