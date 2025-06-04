package com.example.landmarkmanager.data.model

enum class LandmarkCategory(val displayName: String, val apiValue: String) {
    RELIGIOUS("Religious Tourism", "RELIGIOUS"),
    HISTORICAL("Historical", "HISTORICAL"),
    NATURAL("Natural", "NATURAL"),
    CULTURAL("Cultural", "CULTURAL"),
    OTHER("Other", "OTHER");

    companion object {
        fun fromApiValue(value: String): LandmarkCategory {
            return values().find { it.apiValue == value } ?: OTHER
        }
    }
} 