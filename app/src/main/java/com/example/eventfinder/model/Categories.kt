package com.example.eventfinder.model

import com.example.eventfinder.R

object Categories {
    fun getDefaultCategories() = listOf(
        Category(1, "All"),

        Category(2, "Arts & Theatre"),
        Category(3, "Sports"),
        Category(4, "Film", ),
        Category(5, "Fitness"),
        Category(6, "Music")
    )
}