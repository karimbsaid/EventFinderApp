package com.example.eventfinder.model

import com.example.eventfinder.R

object Categories {
    fun getDefaultCategories() = listOf(
        Category(1, "Arts & Theatre", R.drawable.icon),
        Category(2, "Sports", R.drawable.icon),
        Category(3, "Film", R.drawable.icon),
        Category(4, "Fitness", R.drawable.icon),
        Category(5, "Music", R.drawable.icon)
    )
}