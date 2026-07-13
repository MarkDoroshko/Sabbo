package com.example.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicDbModel(@PrimaryKey val topic: String)
